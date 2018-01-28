package cash.xcl.api;

import cash.xcl.api.dto.DtoParser;
import cash.xcl.api.dto.MethodIds;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.TCPServer;
import cash.xcl.net.TCPServerConnectionListener;
import cash.xcl.net.VanillaTCPServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XCLServer implements Closeable {
    final TCPServer tcpServer;
    private final long address;
    private final Bytes secretKey;
    private final AllMessages allMessageListener;
    private final Map<Long, TCPConnection> connections = new ConcurrentHashMap<>();


    public XCLServer(String name, int port, long address, Bytes secretKey, AllMessages allMessageListener) throws IOException {
        this.address = address;
        this.secretKey = secretKey;
        this.allMessageListener = allMessageListener;
        tcpServer = new VanillaTCPServer(name, port, new XCLConnectionListener());
    }

    public void write(long address, SignedMessage message) {
        connections.get(address);
    }

    class XCLConnectionListener implements TCPServerConnectionListener {
        final DtoParser parser = new DtoParser();
        Bytes bytes = Bytes.allocateElasticDirect();

        @Override
        public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
            try {
                long address = bytes.readLong(bytes.readPosition() + 64);
                long messageType = bytes.readUnsignedByte(bytes.readPosition() + 81);
                if (messageType == MethodIds.SUBSCRIPTION_COMMAND) {
                    connections.put(address, channel);
                }

                parser.parseOne(bytes, allMessageListener);

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

    @Override
    public void close() {
        tcpServer.close();
    }
}
