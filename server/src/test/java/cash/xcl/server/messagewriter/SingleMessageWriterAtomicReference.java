package cash.xcl.server.messagewriter;

import cash.xcl.api.dto.GenericSignedMessage;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.MessageWriter;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.threads.LongPauser;
import net.openhft.chronicle.threads.NamedThreadFactory;
import net.openhft.chronicle.threads.Pauser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SingleMessageWriterAtomicReference extends WritingAllMessages implements Runnable, MessageWriter {
    final Pauser pauser = new LongPauser(0, 10, 1, 20, TimeUnit.MILLISECONDS);
    private final XCLServer xclServer;
    private final GenericSignedMessage signedMessage = new GenericSignedMessage();
    private final AtomicReference<Bytes> writeLock = new AtomicReference<>();
    private long addressOrRegion;
    private final Bytes bytes1 = Bytes.allocateElasticDirect(32 << 20).unchecked(true);
    private final Bytes bytes2 = Bytes.allocateElasticDirect(32 << 20).unchecked(true);

    private int sent = 0;

    public SingleMessageWriterAtomicReference(XCLServer xclServer) {
        this.xclServer = xclServer;
        writeLock.set(bytes1);
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
        Bytes bytes = lock();
        try {
            long position = bytes.writePosition();
            bytes.ensureCapacity(position + (1 << 16));
            bytes.writeInt(0);
            bytes.writeLong(addressOrRegion);
            message.writeMarshallable(bytes);
            bytes.writeInt(position, (int) (bytes.writePosition() - position - 4));
        } finally {
            unlock(bytes);
        }
        pauser.unpause();
    }

    private Bytes lock() {
        return writeLock.getAndSet(null);
    }

    private void unlock(Bytes bytes) {
        writeLock.set(bytes);
    }

    boolean flush() {
        Bytes bytes = writeLock.get();
        if (bytes == null)
            return false;
        Bytes other = bytes == bytes1 ? bytes2 : bytes1;
        if (!writeLock.compareAndSet(bytes, other))
            return false;

        if (bytes.writePosition() == 0)
            return false;

        long limit = bytes.readLimit();
        while (bytes.readRemaining() > 0) {
            int size = bytes.readInt();
            long end = bytes.readPosition() + size;
            bytes.readLimit(end);
            try {
                long address = bytes.readLong();
                signedMessage.readMarshallable(bytes);
                xclServer.write(address, signedMessage);

                sent++;

            } finally {
                bytes.readPosition(end);
                bytes.readLimit(limit);
            }
        }
        bytes.clear();
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

