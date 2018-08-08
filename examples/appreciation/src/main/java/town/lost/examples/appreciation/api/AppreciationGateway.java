package town.lost.examples.appreciation.api;

/**
 * Gateway processor which handles queries from the client
 * as well as validate and pass transactions
 */
public interface AppreciationGateway extends AppreciationTransactionListener {
    /**
     * Report the current balance for this public key.
     */
    void queryBalance(QueryBalance queryBalance);
}
