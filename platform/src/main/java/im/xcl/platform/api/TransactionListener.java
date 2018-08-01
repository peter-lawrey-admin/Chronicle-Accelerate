package im.xcl.platform.api;

/**
 * Both the gateway (pre-blockchain) and blockchain processor (post blockchain) should implement this interface.
 */
public interface TransactionListener {
    /**
     * Called when a batch of transactions is started driven by a give public key.
     *
     * @param startBatch containg the public key and time stamp.
     */
    void startBatch(StartBatch startBatch);

    default void onError(String reason) {
    }
    /**
     * End of transaction.
     */
    default void endBatch() {
    }
}
