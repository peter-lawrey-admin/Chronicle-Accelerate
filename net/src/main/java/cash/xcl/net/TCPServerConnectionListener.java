package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

@FunctionalInterface
public interface TCPServerConnectionListener<C extends TCPConnection> {
    default void onNewConnection(TCPServer server, C channel) {

    }

    default void onClosedConnection(TCPServer server, C channel) {

    }

    default C createConnection(TCPServer server, SocketChannel accept) throws SocketException {
        return (C) new VanillaTCPServerConnection(server, accept);
    }


    void onMessage(TCPServer server, C channel, Bytes bytes) throws IOException;
}
