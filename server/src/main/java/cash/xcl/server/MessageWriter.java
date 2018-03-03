package cash.xcl.server;

import cash.xcl.api.dto.GenericSignedMessage;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.threads.LongPauser;
import net.openhft.chronicle.threads.Pauser;

import java.util.concurrent.TimeUnit;

public class MessageWriter extends WritingAllMessages implements Runnable {
    final Pauser pauser = new LongPauser(0, 10, 1, 20, TimeUnit.MILLISECONDS);
    private final Object lock = new Object();
    private final XCLServer xclServer;
    private final GenericSignedMessage signedMessage = new GenericSignedMessage();
    private long addressOrRegion;
    private Bytes bytes1 = Bytes.allocateElasticDirect(32 << 20);
    private Bytes bytes2 = Bytes.allocateElasticDirect(32 << 20);

    public MessageWriter(XCLServer xclServer) {
        this.xclServer = xclServer;
    }

    @Override
    public WritingAllMessages to(long addressOrRegion) {
        this.addressOrRegion = addressOrRegion;
        return this;
    }

    @Override
    public void write(SignedMessage message) {
        synchronized (lock) {
            long position = bytes1.writePosition();
            bytes1.writeInt(0);
            bytes1.writeLong(addressOrRegion);
            message.writeMarshallable(bytes1);
            bytes1.writeInt(position, (int) (bytes1.writePosition() - position - 4));
        }
        pauser.unpause();
    }

    boolean flush() {
        synchronized (lock) {
            if (bytes1.writePosition() == 0)
                return false;
            Bytes tmp = bytes2;
            bytes2 = bytes1;
            bytes1 = tmp;
            bytes1.clear();
        }
        long limit = bytes2.readLimit();
        while (bytes2.readRemaining() > 0) {
            int size = bytes2.readInt();
            long end = bytes2.readPosition() + size;
            bytes2.readLimit(end);
            try {
                long address = bytes2.readLong();
                signedMessage.readMarshallable(bytes2);
                xclServer.write(address, signedMessage);
            } finally {
                bytes2.readPosition(end);
                bytes2.readLimit(limit);
            }
        }
        bytes2.clear();
        return true;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (flush())
                    pauser.reset();
                else
                    pauser.pause();
            }
        } catch (Throwable t) {
            Jvm.warn().on(getClass(), "Writer died", t);
        }
    }
}

