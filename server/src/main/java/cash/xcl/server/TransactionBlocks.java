package cash.xcl.server;

import cash.xcl.api.dto.TransactionBlockEvent;

class TransactionBlocks {
    long lastBlock = 0;

    public void add(TransactionBlockEvent transactionBlockEvent) {
        lastBlock = Math.max(lastBlock, transactionBlockEvent.blockNumber());
    }
}
