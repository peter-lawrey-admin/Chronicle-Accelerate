package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.tcp.WritingAllMessages;

public class VanillaChainer extends WritingAllMessages implements Chainer {
    private final Object transactionLock = new Object();
    private TransactionBlockEvent tbe = new TransactionBlockEvent();
    private TransactionBlockEvent tbe2 = new TransactionBlockEvent();

    public VanillaChainer(String region) {
        tbe.region(region);
        tbe2.region(region);
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void write(SignedMessage message) {
        synchronized (transactionLock) {
//            System.out.println("Add "+message);
            tbe.addTransaction(message);
        }
    }

    public TransactionBlockEvent nextTransactionBlockEvent() {
        TransactionBlockEvent tbeToSend;
        synchronized (transactionLock) {
//            System.out.println(System.currentTimeMillis()+"  TBE count "+tbe.count());
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
