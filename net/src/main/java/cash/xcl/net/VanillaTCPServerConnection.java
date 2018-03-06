package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

public class VanillaTCPServerConnection extends AbstractTCPConnection {
    private final TCPServer tcpServer;

    public VanillaTCPServerConnection(TCPServer tcpServer, SocketChannel channel) throws SocketException {
        super(channel);
        this.tcpServer = tcpServer;
        channel.socket().setSendBufferSize(1 << 20);
        channel.socket().setReceiveBufferSize(2 << 20);
//        channel.socket().setTcpNoDelay(true);
    }

    private static ByteBuffer[] createHeaderArray() {
        ByteBuffer header = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer[] ret = {header, null};
        return ret;
    }

    void run() {
        try {
            tcpServer.connectionListener().onNewConnection(tcpServer, this);
            Bytes<ByteBuffer> readBytes = Bytes.elasticByteBuffer(MAX_MESSAGE_SIZE);

            while (running) {
                readChannel(readBytes);
            }
        } catch (EOFException eof) {
            // connection closed
        } catch (Throwable t) {
            Jvm.pause(1);
            if (running)
                t.printStackTrace();

        } finally {
            tcpServer.connectionListener().onClosedConnection(tcpServer, this);
        }
    }

    @Override
    protected void waitForReconnect() {
        // never reconnects
    }

    @Override
    protected void onMessage(Bytes<ByteBuffer> bytes) throws IOException {
        tcpServer.connectionListener().onMessage(tcpServer, this, bytes);
    }

    @Override
    protected void close2() {

    }
}
