package cash.xcl.server.messagewriter;

import cash.xcl.api.dto.GenericSignedMessage;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.MessageWriter;
import cash.xcl.server.SingleMessageWriter;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.threads.LongPauser;
import net.openhft.chronicle.threads.NamedThreadFactory;
import net.openhft.chronicle.threads.Pauser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleMessageWriterAtomicBoolean extends WritingAllMessages implements Runnable, MessageWriter {
    final Pauser pauser = new LongPauser(0, 10, 1, 20, TimeUnit.MILLISECONDS);
    private final AtomicBoolean spinLock = new AtomicBoolean();
    private final XCLServer xclServer;
    private final GenericSignedMessage signedMessage = new GenericSignedMessage();
    private long addressOrRegion;
    private Bytes bytes1 = Bytes.allocateElasticDirect(32 << 20).unchecked(true);
    private Bytes bytes2 = Bytes.allocateElasticDirect(32 << 20).unchecked(true);


    private int sent = 0;

    public SingleMessageWriterAtomicBoolean(XCLServer xclServer) {
        this.xclServer = xclServer;
    }

    @Override
    public WritingAllMessages to(long addressOrRegion) {
        this.addressOrRegion = addressOrRegion;
        return this;
    }

    @Override
    public Runnable[] runnables() {
        return new Runnable[]{this};
    }

    @Override
    public void write(SignedMessage message) {
        lock();
        try {
            long position = bytes1.writePosition();
            bytes1.ensureCapacity(position + (1 << 16));
            bytes1.writeInt(0);
            bytes1.writeLong(addressOrRegion);
            message.writeMarshallable(bytes1);
            bytes1.writeInt(position, (int) (bytes1.writePosition() - position - 4));
        } finally {
            unlock();
        }
        pauser.unpause();
    }

    private void lock() {
        while (!spinLock.compareAndSet(false, true)) ;
    }

    private void unlock() {
        spinLock.set(false);
    }

    boolean flush() {
        lock();
        try {
            if (bytes1.writePosition() == 0)
                return false;
            Bytes tmp = bytes2;
            bytes2 = bytes1;
            bytes1 = tmp;
            bytes1.clear();
        } finally {
            unlock();
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

                sent++;

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

                if( sent == SingleMessageWriterTest.NUMBER_OF_MESSAGES ) {
                    break;
                }
            }
        } catch (Throwable t) {
            Jvm.warn().on(getClass(), "Writer died", t);
        }
    }


    // only for testing purposes
    private ExecutorService service = Executors.newSingleThreadExecutor(new NamedThreadFactory("-messageWriter", true,Thread.MIN_PRIORITY));
    public Future start() {
        return service.submit(this::run);
    }

}

