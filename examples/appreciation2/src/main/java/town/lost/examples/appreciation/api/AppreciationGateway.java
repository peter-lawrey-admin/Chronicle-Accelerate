package town.lost.examples.appreciation.api;

import net.openhft.chronicle.bytes.MethodId;

/**
 * Gateway processor which handles queries from the client
 * as well as validate and pass transactions
 */
public interface AppreciationGateway extends AppreciationTransactionListener {
    /**
     * Report the current balance for this public key.
     */
    @MethodId(0x2000)
    void queryBalance();
}
