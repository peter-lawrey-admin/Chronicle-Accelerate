package im.xcl.platform.rpc;

import cash.xcl.net.TCPConnection;
import cash.xcl.net.TCPServer;
import cash.xcl.net.TCPServerConnectionListener;
import cash.xcl.net.VanillaTCPServer;
import cash.xcl.util.PublicKeyRegistry;
import cash.xcl.util.VanillaPublicKeyRegistry;
import cash.xcl.util.XCLLongObjMap;
import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.dto.VanillaSignedMessage;
import im.xcl.platform.util.DtoParser;
import im.xcl.platform.util.DtoParserBuilder;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RPCServer<T> implements MessageRouter<T>, PublicKeyRegistry, Closeable {
    private final XCLLongObjMap<TCPConnection> connections = XCLLongObjMap.withExpectedSize(TCPConnection.class, 128);
    private final XCLLongObjMap<TCPConnection> remoteMap = XCLLongObjMap.withExpectedSize(TCPConnection.class, 128);
    private final Map<Long, T> allMessagesMap = new ConcurrentHashMap<>();
    private final PublicKeyRegistry publicKeyRegistry = new VanillaPublicKeyRegistry();
    private final TCPServer tcpServer;
    private final int port;
    private final long address;
    private final BytesStore publicKey;
    private final BytesStore secretKey;
    private final DtoParserBuilder<T> dtoParserBuilder;
    private final T serverComponent;

    public RPCServer(String name, int port, long address, BytesStore publicKey, BytesStore secretKey, DtoParserBuilder<T> dtoParserBuilder, Function<MessageRouter<T>, T> serverComponentBuilder) throws IOException {
        this.port = port;
        this.address = address;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
        this.dtoParserBuilder = dtoParserBuilder;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener(dtoParserBuilder.get()));
        this.serverComponent = serverComponentBuilder.apply(this);
    }

    public boolean internal() {
        return publicKeyRegistry.internal();
    }

    public RPCServer internal(boolean internal) {
        publicKeyRegistry.internal(internal);
        return this;
    }

    /**
     * Add known connections between clusters
     *
     * @param addressOrRegion to associate with this connection
     * @param tcpConnection   to connect to.
     */
    public void addTCPConnection(long addressOrRegion, TCPConnection tcpConnection) {
        synchronized (remoteMap) {
            remoteMap.put(addressOrRegion, tcpConnection);
        }
    }

    @Override
    public T to(long addressOrRegion) {
//        return allMessagesMap.computeIfAbsent(addressOrRegion, OneWritingAllMessages::new);
        throw new UnsupportedOperationException();
    }

    private long address() {
        return address;
    }

    @Override
    public void close() {
        synchronized (connections) {
            connections.forEach((k, connection) ->
                    Closeable.closeQuietly(connection));
            connections.clear();
        }
        synchronized (remoteMap) {
            remoteMap.forEach((k, connection) ->
                    Closeable.closeQuietly(connection));
            remoteMap.clear();
        }
        tcpServer.close();
    }

    @Override
    public void register(long address, Bytes<?> publicKey) {
        publicKeyRegistry.register(address, publicKey);
    }

    @Override
    public Boolean verify(long address, Bytes<?> sigAndMsg) {
        return publicKeyRegistry.verify(address, sigAndMsg);
    }

    public void write(long toAddress, VanillaSignedMessage message) {
        TCPConnection tcpConnection;
        synchronized (connections) {
            tcpConnection = connections.get(toAddress);
        }
        if (tcpConnection == null) {
            synchronized (remoteMap) {
                tcpConnection = remoteMap.get(toAddress);
            }
        }

        if (tcpConnection == null) {
            System.out.println(address + " - No connection to address " + toAddress + " to send " + message);
            return;
        }

        try {

            if (!message.signed()) {
                message.sign(secretKey);
            }
            tcpConnection.write(message.byteBuffer());

        } catch (IllegalStateException e2) {
            e2.printStackTrace();
            System.err.println("Failed to marshall object " + e2.toString());
            // we should never get IllegalStateException exceptions, but if we do,
            // rethrow the exception so that the LocalPostBlockChainProcessor can send a CommandFailedEvent back to the Client
            throw e2;
        } catch (Exception e) {
            e.printStackTrace();
            // assume it's dead.
            Closeable.closeQuietly(tcpConnection);
            synchronized (connections) {
                connections.remove(toAddress);
            }
            Jvm.warn().on(getClass(), "Exception while sending message to: " + toAddress + ", message: " + message, e);
        }
    }

    class XCLConnectionListener implements TCPServerConnectionListener {
        final DtoParser<T> dtoParser;

        XCLConnectionListener(DtoParser<T> dtoParser) {
            this.dtoParser = dtoParser;
        }

        @Override
        public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
            bytes.readSkip(-4);
            try {

                dtoParser.parseOne(bytes, serverComponent);

            } catch (IORuntimeException iore) {
                if (iore.getCause() instanceof IOException)
                    throw (IOException) iore.getCause();
                throw iore;
            }
        }
    }
}
