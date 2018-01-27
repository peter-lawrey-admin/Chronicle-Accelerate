package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;

import java.io.IOException;

@FunctionalInterface
public interface TCPServerConnectionListener {
    default void onNewConnection(TCPServer server, TCPConnection channel) {

    }

    default void onClosedConnection(TCPServer server, TCPConnection channel) {

    }

    void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) throws IOException;
}
