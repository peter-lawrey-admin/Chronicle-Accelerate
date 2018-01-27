package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface TCPClientListener {
    default void onClosedChannel(TCPConnection client) {

    }

    default void onNewConnection(TCPConnection client, SocketChannel channel) {

    }

    void onMessage(TCPConnection client, Bytes bytes) throws IOException;
}
