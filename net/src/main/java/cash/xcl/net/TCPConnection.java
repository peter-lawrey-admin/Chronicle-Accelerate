package cash.xcl.net;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.Closeable;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface TCPConnection extends Closeable {
    int MAX_MESSAGE_SIZE = 1 << 20;
    int HEADER_LENGTH = 4;

    void write(BytesStore<?, ByteBuffer> bytes) throws IOException;

    void write(ByteBuffer buffer) throws IOException;
}
