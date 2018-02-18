package cash.xcl.server;

import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TreeBlockEvent;

public interface BlockReplayer {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void replayBlocks() throws InterruptedException;
}
