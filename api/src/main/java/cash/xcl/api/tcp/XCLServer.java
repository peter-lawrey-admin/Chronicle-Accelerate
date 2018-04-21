package cash.xcl.api.tcp;

import cash.xcl.api.*;
import cash.xcl.api.dto.BaseDtoParser;
import cash.xcl.api.dto.SignedBinaryMessage;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.TCPServer;
import cash.xcl.net.TCPServerConnectionListener;
import cash.xcl.net.VanillaTCPServer;
import cash.xcl.util.PublicKeyRegistry;
import cash.xcl.util.VanillaPublicKeyRegistry;
import cash.xcl.util.XCLLongObjMap;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static cash.xcl.api.DtoParser.MESSAGE_OFFSET;
import static cash.xcl.api.dto.MessageTypes.*;

public class XCLServer implements AllMessagesLookup, PublicKeyRegistry, Closeable {
    final Map<Integer, Supplier<DtoParser<?>>> protocolDtoParserSupplierMap = new ConcurrentHashMap<>();
    final ThreadLocal<DtoParser> parserTL = new ThreadLocal<>();
    private final long address;
    private final Bytes secretKey;
    private final AllMessagesServer serverComponent;
    private final XCLLongObjMap<TCPConnection> connections = XCLLongObjMap.withExpectedSize(TCPConnection.class, 128);
    private final XCLLongObjMap<TCPConnection> remoteMap = XCLLongObjMap.withExpectedSize(TCPConnection.class, 128);
    private final Map<Long, WritingAllMessages> allMessagesMap = new ConcurrentHashMap<>();
    private final PublicKeyRegistry publicKeyRegistry = new VanillaPublicKeyRegistry();
    private final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);
    private final TCPServer tcpServer;
    private final Bytes publicKey;
    private final int port;
    boolean useBaseDtoParser = true;

    public boolean internal() {
        return publicKeyRegistry.internal();
    }

    public XCLServer internal(boolean internal) {
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
    public WritingAllMessages to(long addressOrRegion) {
        return allMessagesMap.computeIfAbsent(addressOrRegion, OneWritingAllMessages::new);
    }

    public XCLServer(String name, int port, long address, Bytes publicKey, Bytes secretKey, AllMessagesServer serverComponent) throws IOException {
        this.port = port;
        this.address = address;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
        this.serverComponent = serverComponent;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener());

        // do this last after initialisation.
        serverComponent.allMessagesLookup(this);

        protocolDtoParserSupplierMap.put(1, BaseDtoParser::new);
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
        Closeable.closeQuietly(serverComponent);
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

    public void write(long toAddress, SignedBinaryMessage message) {
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

            if (!message.hasSignature()) {
                message.eventTime(SystemTimeProvider.INSTANCE.currentTimeMicros());
                Bytes bytes = bytesTL.get();
                bytes.clear();
                message.sign(bytes, address(), secretKey);
            }
            tcpConnection.write(message.sigAndMsg());

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

    public void addDtoParser(int protocol, Supplier<DtoParser<?>> dtoParserSupplier) {
        protocolDtoParserSupplierMap.put(protocol, dtoParserSupplier);
        useBaseDtoParser = false;
    }

    private DtoParser dtoParser() {
        DtoParser dtoParser = parserTL.get();
        return dtoParser == null ? initDtoParser() : dtoParser;
    }

    @NotNull
    private DtoParser initDtoParser() {
        DtoParser dtoParser = useBaseDtoParser
                ? new BaseDtoParser()
                : new MultiDtoParser(protocolDtoParserSupplierMap);
        parserTL.set(dtoParser);
        return dtoParser;
    }

    public <G> G gatewayFor(Class<G> gClass) {
        if (gClass.isAssignableFrom(AllMessages.class))
            return (G) serverComponent;
        throw new IllegalArgumentException("No such gateway for " + gClass);
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public int port() {
        return port;
    }

    class XCLConnectionListener implements TCPServerConnectionListener {

        @Override
        public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
            try {
                long address = bytes.readLong(bytes.readPosition() + Ed25519.SIGNATURE_LENGTH);
                long messageType = bytes.readUnsignedByte(bytes.readPosition() + MESSAGE_OFFSET);
                // todo
                // this is a quick workaround - don't verify CreateNewAddressEvent messages
                // as they are currently used to register the public key.
                if (messageType != CREATE_NEW_ADDRESS_EVENT) {
                    Boolean verify = publicKeyRegistry.verify(address, bytes);
                    if (verify == null || !verify) {
                        System.err.println("Verify: " + verify + " for address " + address + " and  object of message type " + messageType);
                        return;
                    }
                }
                if (messageType == SUBSCRIPTION_QUERY ||
                        messageType == CURRENT_BALANCE_QUERY ||
                        messageType == EXCHANGE_RATE_QUERY ||
                        messageType == CLUSTER_STATUS_QUERY ||
                        messageType == CLUSTERS_STATUS_QUERY) {
                    synchronized (connections) {
                        connections.put(address, channel);
                    }
                }

                dtoParser().parseOne(bytes, serverComponent);

            } catch (ClientException ce) {
                SignedBinaryMessage message = ce.message();
                try {
                    if (!message.hasSignature()) {
                        bytes.clear();
                        message.sign(bytes, address, secretKey);
                    }
                    channel.write(message.sigAndMsg());

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }

            } catch (IORuntimeException iore) {
                if (iore.getCause() instanceof IOException)
                    throw (IOException) iore.getCause();
                throw iore;
            }
        }
    }

    private class OneWritingAllMessages extends WritingAllMessages {
        private final long address;

        public OneWritingAllMessages(long address) {
            this.address = address;
        }

        @Override
        public WritingAllMessages to(long addressOrRegion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(SignedBinaryMessage message) {
            XCLServer.this.write(address, message);
        }

        @Override
        public void write(long address, SignedBinaryMessage message) {
            XCLServer.this.write(address, message);
        }

        @Override
        public void close() {

        }
    }
}
