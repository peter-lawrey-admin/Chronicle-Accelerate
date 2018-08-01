package town.lost.examples.verifyip.api;

import im.xcl.platform.api.TransactionListener;
import net.openhft.chronicle.bytes.MethodId;

public interface VerifyIP extends TransactionListener {
    /**
     * Called when a connection is first made
     */
    void onConnection();

    /**
     * Send a verification message which can be signed
     */
    @MethodId(~0x80)
    void verify(Verify verify);

    /**
     * Notify that a server was invalidated
     */
    @MethodId(~0x81)
    void invalidation(Invalidation record);
}
