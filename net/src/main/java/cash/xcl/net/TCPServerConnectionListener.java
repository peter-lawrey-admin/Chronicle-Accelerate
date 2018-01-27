package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;

public interface TCPServerConnectionListener {
    default void onNewConnection(TCPServer server, TCPConnection channel) {

    }

    default void onClosedConnection(TCPServer server, TCPConnection channel) {

    }

    default void onMessage(TCPServer server, TCPConnection channel, Bytes bytes) {

    }
}
