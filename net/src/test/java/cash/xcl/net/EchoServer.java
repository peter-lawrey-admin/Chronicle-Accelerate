package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;

import java.io.IOException;

public class EchoServer implements Closeable, TCPServerConnectionListener {
    static final int PORT = Integer.getInteger("port", 9090);

    private final VanillaTCPServer server;

    public EchoServer(String name, int port) throws IOException {
        server = new VanillaTCPServer(name, port, this);
    }

    public static void main(String... args) throws IOException {
        new EchoServer("echo", PORT);
    }

    @Override
    public void onNewConnection(TCPServer server, TCPConnection channel) {
        System.out.println("Connected " + channel);
    }

    @Override
    public void onClosedConnection(TCPServer server, TCPConnection channel) {
        System.out.println("... Disconnected " + channel);
    }

    public void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException {
        System.out.println("+ " + bytes);
        channel.write(bytes);
    }

    @Override
    public void close() {
        server.close();
    }
}
