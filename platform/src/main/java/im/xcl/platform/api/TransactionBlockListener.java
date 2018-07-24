package im.xcl.platform.api;

public interface TransactionBlockListener {
    /**
     * start a transaction block
     */
    void startBlock(StartBlock startBlock);

    void endBlock();
}
