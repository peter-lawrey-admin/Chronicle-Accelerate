package town.lost.examples.appreciation.api;

/**
 * Transactions passed through the block chain
 */
public interface AppreciationTransactionListener {

    /**
     * Report the current balance for this public key.
     */
    void openingBalance(OpeningBalance openingBalance);

    void give(Give give);
}
