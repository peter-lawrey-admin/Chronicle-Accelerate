package cash.xcl.server.chain;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;
import cash.xcl.api.tcp.WritingAllMessages;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Chainer extends WritingAllMessages implements ServerComponent, Runnable {
    private final int periodMS;
    private final long[] addresses;
    private final Object transactionLock = new Object();
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("chain", true));
    private final Consumer<TransactionBlockEvent> tbeConsumer;
    final TransactionBlockGossipEvent tbge = new TransactionBlockGossipEvent();
    private final String region;
    private final Map<Long, TransactionBlocks> transactionBlocksMap = new ConcurrentHashMap<>();
    private AllMessagesLookup lookup;
    private TransactionBlockEvent tbe = new TransactionBlockEvent();
    private TransactionBlockEvent tbe2 = new TransactionBlockEvent();
    private long nextSend;
    private long blockNumber = 0;

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

    public Chainer(String region, int periodMS, long[] addresses, Consumer<TransactionBlockEvent> tbeConsumer) {
        this.periodMS = periodMS;
        this.addresses = addresses;
        nextSend = System.currentTimeMillis() / periodMS * periodMS + periodMS;
        this.tbeConsumer = tbeConsumer;
        this.region = region;

        ses.submit(this);
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        TransactionBlocks tb = transactionBlocksMap.computeIfAbsent(
                transactionBlockEvent.sourceAddress(),
                k -> new TransactionBlocks());
        tb.add(transactionBlockEvent);
    }

    @Override
    public void run() {
        try {
            sendTransactionBlockEvent();
            Jvm.pause(Math.max(1, periodMS / 4));
            tbge.blockNumber(blockNumber++);
            Map<Long, Long> longLongMap = tbge.addressToBlockNumberMap();
            longLongMap.clear();
            for (Map.Entry<Long, TransactionBlocks> entry : transactionBlocksMap.entrySet()) {
                longLongMap.put(entry.getKey(), entry.getValue().lastBlock);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            nextSend += periodMS;
            long delay = nextSend - SystemTimeProvider.INSTANCE.currentTimeMillis();
            ses.schedule(this, delay, TimeUnit.MILLISECONDS);
        }
    }

    private void sendTransactionBlockEvent() {
        TransactionBlockEvent tbeToSend;
        synchronized (transactionLock) {
            if (tbe.count() == 0)
                return;
            tbeToSend = tbe;
            tbe = tbe2;
            tbe2 = tbeToSend;
            tbe.clear();
        }
        tbeToSend.region(region);
        tbeToSend.blockNumber(blockNumber++);
        System.out.println("Sending " + tbeToSend.count() + " transactions");
        tbeConsumer.accept(tbeToSend);
        for (long address : addresses) {
            try {
                lookup.to(address).transactionBlockEvent(tbeToSend);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void close() {
        ses.shutdownNow();
    }

    private class TransactionBlocks {
        long lastBlock = 0;

        public void add(TransactionBlockEvent transactionBlockEvent) {
            lastBlock = Math.max(lastBlock, transactionBlockEvent.blockNumber());
        }
    }
}
