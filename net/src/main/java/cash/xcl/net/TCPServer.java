package cash.xcl.net;

import net.openhft.chronicle.core.io.Closeable;

public interface TCPServer extends Closeable {
    TCPServerConnectionListener connectionListener();
}
