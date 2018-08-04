package town.lost.examples.appreciation.api;

import im.xcl.platform.api.TransactionListener;
import net.openhft.chronicle.bytes.MethodId;

/**
 * Transactions passed through the block chain
 */
public interface AppreciationTransactionListener extends TransactionListener {

    /**
     * Report the current balance for this public key.
     */
    @MethodId(0x0080)
    void openingBalance(OpeningBalance openingBalance);

    @MethodId(0x0100)
    void give(Give give);
}
