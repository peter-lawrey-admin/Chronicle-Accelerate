package cash.xcl.server.chain;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.tcp.WritingAllMessages;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Chainer extends WritingAllMessages implements ServerComponent, Runnable {
    private final int periodMS;
    private final long[] addresses;
    private final Object transactionLock = new Object();
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("chain", true));
    private AllMessagesLookup lookup;
    private TransactionBlockEvent tbe = new TransactionBlockEvent();
    private TransactionBlockEvent tbe2 = new TransactionBlockEvent();
    private long nextSend;

    public Chainer(int periodMS, long[] addresses) {
        this.periodMS = periodMS;
        this.addresses = addresses;
        nextSend = System.currentTimeMillis() / periodMS * periodMS + periodMS;

        ses.submit(this);
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void write(SignedMessage message) {
        synchronized (transactionLock) {
            tbe.addTransaction(message);
        }
    }

    @Override
    public void run() {
        try {
            TransactionBlockEvent tbeToSend;
            synchronized (transactionLock) {
                if (tbe.count() == 0)
                    return;
                tbeToSend = tbe;
                tbe = tbe2;
                tbe2 = tbeToSend;
                tbe.clear();
            }
            System.out.println("Sending " + tbeToSend.count() + " transactions");
            for (long address : addresses) {
                lookup.to(address).transactionBlockEvent(tbeToSend);
            }
        } finally {
            nextSend += periodMS;
            long delay = nextSend - SystemTimeProvider.INSTANCE.currentTimeMillis();
            ses.schedule(this, delay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void close() {
        ses.shutdownNow();
    }
}
