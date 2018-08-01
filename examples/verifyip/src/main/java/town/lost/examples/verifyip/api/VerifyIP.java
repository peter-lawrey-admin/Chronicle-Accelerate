package town.lost.examples.verifyip.api;

import im.xcl.platform.api.TransactionListener;

public interface VerifyIP extends TransactionListener {
    /**
     * Send a verification message which can be signed
     */
    void verify(Verify verify);

    /**
     * Notify that a server was invalidated
     */
    void invalidation(Invalidation record);
}
