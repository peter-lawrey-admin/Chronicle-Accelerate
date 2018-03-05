package cash.xcl.server;

import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.tcp.WritingAllMessages;

import java.util.LinkedList;
import java.util.Queue;

public class QueuingChainer extends WritingAllMessages implements Chainer {
    private final Object transactionLock = new Object();
    private TransactionBlockEvent tbe = new TransactionBlockEvent();
    private TransactionBlockEvent tbe2 = new TransactionBlockEvent();
    private Queue<TransactionBlockEvent> queue = new LinkedList<>();

    private int region;

    public QueuingChainer(int region) {
        tbe.region(region);
        tbe2.region(region);
        this.region = region;
    }

    @Override
    public WritingAllMessages to(long addressOrRegion) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(SignedMessage message) {
        synchronized (transactionLock) {
            if (tbe.isBufferFull()) {
                System.out.println("buffer is full - creating new block");
                this.queue.add(tbe);
                tbe = new TransactionBlockEvent();
                tbe.region(region);
            }
            tbe.addTransaction(message);
        }
    }

    public TransactionBlockEvent nextTransactionBlockEvent() {
        TransactionBlockEvent tbeToSend;
        String name = Thread.currentThread().getName();
        synchronized (transactionLock) {
            if (queue.size() > 0)
                System.out.println(name + " - tbe's queue size = " + queue.size());
            if (tbe.count() > 0)
                System.out.println(name + " - current tbe count = " + tbe.count());
            if (!queue.isEmpty()) {
                return queue.poll();
            }

            if (tbe.count() == 0)
                return null;
            tbeToSend = tbe;
            tbe = tbe2;
            tbe2 = tbeToSend;
            tbe.reset();
        }
        return tbeToSend;
    }

    @Override
    public void close() {

    }
}
