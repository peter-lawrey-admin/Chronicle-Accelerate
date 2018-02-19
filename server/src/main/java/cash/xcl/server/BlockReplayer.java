package cash.xcl.server;

import cash.xcl.api.dto.EndOfRoundBlockEvent;
import cash.xcl.api.dto.TransactionBlockEvent;

public interface BlockReplayer {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent);

    void replayBlocks() throws InterruptedException;
}
