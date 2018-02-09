package cash.xcl.api.tcp;

import cash.xcl.api.AllMessageLookup;
import cash.xcl.api.AllMessages;
import cash.xcl.api.ClientException;
import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.DtoParser;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.TCPServer;
import cash.xcl.net.TCPServerConnectionListener;
import cash.xcl.net.VanillaTCPServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cash.xcl.api.dto.DtoParser.MESSAGE_OFFSET;
import static cash.xcl.api.dto.MessageTypes.*;

public class XCLServer implements AllMessageLookup, Closeable {
    final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);

    final TCPServer tcpServer;
    private final long address;
    private final Bytes secretKey;
    private final ServerComponent serverComponent;
    private final Map<Long, TCPConnection> connections = new ConcurrentHashMap<>();
    private final Map<Long, TCPConnection> remoteMap = new ConcurrentHashMap<>();
    private final Map<Long, AllMessages> allMessagesMap = new ConcurrentHashMap<>();

    public XCLServer(String name, int port, long address, Bytes secretKey, ServerComponent serverComponent) throws IOException {
        this.address = address;
        this.secretKey = secretKey;
        this.serverComponent = serverComponent;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener());

        // do this last after initialisation.
        serverComponent.allMessageLoopkup(this);
    }

    /**
     * Add known connections between clusters
     *
     * @param addressOrRegion to associate with this connection
     * @param tcpConnection   to connect to.
     */
    public void addTCPConnection(long addressOrRegion, TCPConnection tcpConnection) {
        remoteMap.put(addressOrRegion, tcpConnection);
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        return allMessagesMap.computeIfAbsent(addressOrRegion, OneWritingAllMessages::new);
    }

    public void write(long address, SignedMessage message) {
        Long addressLong = address;
        TCPConnection tcpConnection = connections.get(addressLong);
        if (tcpConnection == null)
            tcpConnection = remoteMap.get(addressLong);

        if (tcpConnection == null)
            return;

        try {

            if (!message.hasSignature()) {
                Bytes bytes = bytesTL.get();
                bytes.clear();
                message.sign(bytes, address, secretKey);
            }
            tcpConnection.write(message.sigAndMsg());

        } catch (Exception e) {
            // assume it's dead.
            Closeable.closeQuietly(tcpConnection);
            connections.remove(address);
            Jvm.warn().on(getClass(), "Unable to write to " + address + " " + message, e);
        }
    }

    @Override
    public void close() {
        tcpServer.close();
    }

    class XCLConnectionListener implements TCPServerConnectionListener {
        final DtoParser parser = new DtoParser();

        @Override
        public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
            try {
                long address = bytes.readLong(bytes.readPosition() + 64);
                long messageType = bytes.readUnsignedByte(bytes.readPosition() + MESSAGE_OFFSET);
                if (messageType == SUBSCRIPTION_QUERY ||
                        messageType == CURRENT_BALANCE_QUERY ||
                        messageType == EXCHANGE_RATE_QUERY ||
                        messageType == CLUSTER_STATUS_QUERY ||
                        messageType == CLUSTERS_STATUS_QUERY) {
                    connections.put(address, channel);
                }

                parser.parseOne(bytes, serverComponent);

            } catch (ClientException ce) {
                SignedMessage message = ce.message();
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
        public AllMessages to(long addressOrRegion) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void write(SignedMessage message) {
            XCLServer.this.write(address, message);
        }

        @Override
        public void close() {

        }
    }
}
