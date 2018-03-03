package cash.xcl.api.tcp;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.ClientException;
import cash.xcl.api.dto.DtoParser;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.util.PublicKeyRegistry;
import cash.xcl.api.util.VanillaPublicKeyRegistry;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.TCPServer;
import cash.xcl.net.TCPServerConnectionListener;
import cash.xcl.net.VanillaTCPServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cash.xcl.api.dto.DtoParser.MESSAGE_OFFSET;
import static cash.xcl.api.dto.MessageTypes.*;

public class XCLServer implements AllMessagesLookup, PublicKeyRegistry, Closeable {
    final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);

    final TCPServer tcpServer;
    private final long address;
    private final Bytes secretKey;
    private final AllMessagesServer serverComponent;
    private final Map<Long, TCPConnection> connections = new ConcurrentHashMap<>();
    private final Map<Long, TCPConnection> remoteMap = new ConcurrentHashMap<>();
    private final Map<Long, WritingAllMessages> allMessagesMap = new ConcurrentHashMap<>();
    private final PublicKeyRegistry publicKeyRegistry = new VanillaPublicKeyRegistry();

    public XCLServer(String name, int port, long address, Bytes secretKey, AllMessagesServer serverComponent) throws IOException {
        this.address = address;
        this.secretKey = secretKey;
        this.serverComponent = serverComponent;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener());

        // do this last after initialisation.
        serverComponent.allMessagesLookup(this);
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
    public WritingAllMessages to(long addressOrRegion) {
        return allMessagesMap.computeIfAbsent(addressOrRegion, OneWritingAllMessages::new);
    }

    public void write(long toAddress, SignedMessage message) {
        Long addressLong = toAddress;
        TCPConnection tcpConnection = connections.get(addressLong);
        if (tcpConnection == null)
            tcpConnection = remoteMap.get(addressLong);

        if (tcpConnection == null) {
            System.out.println(address + " - No connection to address " + addressLong + " to send " + message);
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
            connections.remove(toAddress);
            Jvm.warn().on(getClass(), "Exception while sending message to: " + toAddress + ", message: " + message, e);
        }
    }

    private long address() {
        return address;
    }

    @Override
    public void close() {
        for (TCPConnection connection : remoteMap.values()) {
            Closeable.closeQuietly(connection);
        }
        Closeable.closeQuietly(serverComponent);
        remoteMap.clear();
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

    class XCLConnectionListener implements TCPServerConnectionListener {
        final ThreadLocal<DtoParser> parserTL = ThreadLocal.withInitial(DtoParser::new);

        @Override
        public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
            try {
                long address = bytes.readLong(bytes.readPosition() + Ed25519.SIGNATURE_LENGTH);
                long messageType = bytes.readUnsignedByte(bytes.readPosition() + MESSAGE_OFFSET);
                Boolean verify = publicKeyRegistry.verify(address, bytes);
                if (verify == null || !verify) {
                    System.err.println("Verify: " + verify + " for address " + address + " and  object of message type " + messageType);
                    return;
                }
                if (messageType == SUBSCRIPTION_QUERY ||
                        messageType == CURRENT_BALANCE_QUERY ||
                        messageType == EXCHANGE_RATE_QUERY ||
                        messageType == CLUSTER_STATUS_QUERY ||
                        messageType == CLUSTERS_STATUS_QUERY) {
                    connections.put(address, channel);
                }

                parserTL.get().parseOne(bytes, serverComponent);

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
        public WritingAllMessages to(long addressOrRegion) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(SignedMessage message) {
            XCLServer.this.write(address, message);
        }

        @Override
        public void write(long address, SignedMessage message) {
            XCLServer.this.write(address, message);
        }

        @Override
        public void close() {

        }
    }
}
