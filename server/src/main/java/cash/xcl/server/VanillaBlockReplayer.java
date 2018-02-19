package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.EndOfRoundBlockEvent;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.TransactionBlockEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VanillaBlockReplayer implements BlockReplayer {
    private final long address;
    private final AllMessages postBlockChainProcessor;
    private Map<Long, TransactionLog> transactionLogMap = new ConcurrentHashMap<>();
    private EndOfRoundBlockEvent lastEndOfRoundBlockEvent = null;
    private Map<Long, Long> replayedMap = new LinkedHashMap<>();

    public VanillaBlockReplayer(long address, AllMessages postBlockChainProcessor) {
        this.address = address;
        this.postBlockChainProcessor = postBlockChainProcessor;
    }

    @Override
    public synchronized void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        transactionLogMap.computeIfAbsent(transactionBlockEvent.sourceAddress(), k -> new TransactionLog())
                .add(transactionBlockEvent);
        notifyAll();
    }

    @Override
    public synchronized void treeBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
        transactionLogMap.computeIfAbsent(endOfRoundBlockEvent.sourceAddress(), k -> new TransactionLog())
                .add(endOfRoundBlockEvent);
        lastEndOfRoundBlockEvent = endOfRoundBlockEvent;
        notifyAll();
    }

    @Override
    public void replayBlocks() throws InterruptedException {
        List<Runnable> replayActions = new ArrayList<>();
        synchronized (this) {
            for (Map.Entry<Long, TransactionLog> entry : transactionLogMap.entrySet()) {
                Long upto = lastEndOfRoundBlockEvent.blockRecords().get(entry.getKey());
                if (upto == null) {
                    continue;
                }

                long last = replayedMap.getOrDefault(entry.getKey(), -1L);

                int size;
                while (true) {
                    size = entry.getValue().messages.size();
                    if (size >= upto)
                        break;
                    System.out.println(address + " Waiting ... " + size + " < " + upto);
                    wait(100);
                }

                if (last < size) {
                    replayActions.add(() -> replay(entry.getValue(), last + 1, upto));
                    replayedMap.put(entry.getKey(), upto);
                }
            }
        }
        for (Runnable replayAction : replayActions) {
            replayAction.run();
        }
    }

    private void replay(TransactionLog messages, long fromIndex, long toIndex) {
        for (long i = fromIndex; i <= toIndex; i++) {
            SignedMessage message = messages.get((int) i);
            if (message instanceof TransactionBlockEvent) {
                TransactionBlockEvent tbe = (TransactionBlockEvent) message;
                tbe.replay(postBlockChainProcessor);
            }
        }

    }

    static class TransactionLog {
        private final List<SignedMessage> messages = new ArrayList<>();

        public void add(TransactionBlockEvent transactionBlockEvent) {
            add(transactionBlockEvent, (int) transactionBlockEvent.blockNumber());
        }

        public void add(EndOfRoundBlockEvent endOfRoundBlockEvent) {
            add(endOfRoundBlockEvent, (int) endOfRoundBlockEvent.blockNumber());
        }

        synchronized void add(SignedMessage msg, int blockNumber) {
            if (blockNumber < messages.size())
                System.out.println("Duplicate message id: " + blockNumber + " " + msg.getClassName());
            else if (blockNumber > messages.size())
                System.out.println("Missing message id: " + blockNumber);
            else
                messages.add(msg.deepCopy());
        }

        synchronized SignedMessage get(int index) {
            return messages.get(index);
        }
    }
}
