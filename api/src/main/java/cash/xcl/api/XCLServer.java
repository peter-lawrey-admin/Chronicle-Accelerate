package cash.xcl.api;

import cash.xcl.api.dto.DtoParser;
import cash.xcl.api.dto.MethodIds;
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

public class XCLServer implements Closeable {
    final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);

    final TCPServer tcpServer;
    private final long address;
    private final Bytes secretKey;
    private final ServerComponent serverComponent;
    private final Map<Long, TCPConnection> connections = new ConcurrentHashMap<>();


    public XCLServer(String name, int port, long address, Bytes secretKey, ServerComponent serverComponent) throws IOException {
        this.address = address;
        this.secretKey = secretKey;
        this.serverComponent = serverComponent;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener());

        // do this last after initialisation.
        serverComponent.xclServer(this);
    }

    public void write(long address, SignedMessage message) {
        TCPConnection tcpConnection = connections.get(address);
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
                long messageType = bytes.readUnsignedByte(bytes.readPosition() + 81);
                if (messageType == MethodIds.SUBSCRIPTION_COMMAND) {
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
}
