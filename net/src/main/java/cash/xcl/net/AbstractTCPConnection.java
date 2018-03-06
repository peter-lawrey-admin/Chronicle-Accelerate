package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.tcp.ISocketChannel;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

public abstract class AbstractTCPConnection implements TCPConnection {
    final ThreadLocal<ByteBuffer[]> headerBytesTL = ThreadLocal.withInitial(AbstractTCPConnection::createHeaderArray);

    protected volatile SocketChannel channel;
    protected ISocketChannel iSocketChannel;

    protected AbstractTCPConnection(SocketChannel channel) {
        channel(channel);
    }
    volatile boolean running = true;

    protected AbstractTCPConnection() {
    }

    public AbstractTCPConnection channel(SocketChannel channel) {
        if (channel == null) {
            this.channel = channel;
            iSocketChannel = null;
        } else {
            assert channel != null;
            iSocketChannel = ISocketChannel.wrap(channel);
            this.channel = channel;
        }
        return this;
    }

    private static ByteBuffer[] createHeaderArray() {
        ByteBuffer header = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        ByteBuffer[] ret = {header, null};
        return ret;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + channel;
    }

    @Override
    public void write(Bytes<ByteBuffer> bytes) throws IOException {
        if (!running)
            throw new IOException("closed");

        waitForReconnect();

        if (bytes.readRemaining() > MAX_MESSAGE_SIZE - 4)
            throw new IOException("Message too long " + bytes.readRemaining());
        ByteBuffer buffer = bytes.underlyingObject();
        assert buffer != null;
        buffer.limit(Math.toIntExact(bytes.readLimit()));
        buffer.position(Math.toIntExact(bytes.readPosition()));
        ByteBuffer[] headerBytes = headerBytesTL.get();
        headerBytes[0].clear();
        headerBytes[0].putInt(0, HEADER_LENGTH + buffer.remaining());
        headerBytes[1] = buffer;

        while (buffer.remaining() > 0 && running) {
            if (iSocketChannel.write(headerBytes) < 0) {
                channel.close();
                throw new EOFException("Failed to write");
            }
        }
    }

    protected abstract void waitForReconnect() throws IOException;

    protected void readChannel(Bytes<ByteBuffer> bytes) throws IOException {
        if (bytes.readRemaining() >= HEADER_LENGTH) {
            // length includes the header itself.
            int length = bytes.readInt(bytes.readPosition());
            if (length < HEADER_LENGTH || length > MAX_MESSAGE_SIZE)
                throw new StreamCorruptedException("length: " + length);
            if (bytes.readRemaining() >= length) {
                processOneMessage(length, bytes);
                return;
            }
        }
        if (bytes.readRemaining() == 0)
            bytes.clear(); // reset the position
        else if (bytes.readPosition() > 32 << 10)
            bytes.compact(); // shift the data down.
        ByteBuffer buffer = bytes.underlyingObject();
        buffer.position(Math.toIntExact(bytes.writePosition()));
        buffer.limit(Math.toIntExact(bytes.realCapacity()));
        if (iSocketChannel.read(buffer) < 0)
            throw new EOFException();
        bytes.readLimit(buffer.position());
    }

    private void processOneMessage(int length, Bytes<ByteBuffer> bytes) throws IOException {
        long end = bytes.readPosition() + length;
        long limit = bytes.readLimit();
        try {
            bytes.readSkip(HEADER_LENGTH); // skip the header
            bytes.readLimit(end);
            onMessage(bytes);

        } finally {
            bytes.readLimit(limit);
            bytes.readPosition(end);
        }
    }

    protected abstract void onMessage(Bytes<ByteBuffer> bytes) throws IOException;

    @Override
    public final void close() {
        running = false;
        close2();
        Closeable.closeQuietly(channel);
    }

    protected abstract void close2();
}
