package im.xcl.platform.api;

/**
 * The blockchain processor should implement this interface
 */
public interface TransactionBlockListener {
    /**
     * start a transaction block containing batches of transaction.
     */
    default void startBlock(StartBlock startBlock) {
    }

    /**
     * End of a block.
     */
    default void endBlock() {
    }
}
