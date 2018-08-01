package town.lost.examples.verifyip.api;

import im.xcl.platform.api.TransactionListener;

public interface VerifyIPResponse extends TransactionListener {
    /**
     * Notify that this node has been verified
     *
     * @param verifyIPRecord recording that this node has been verified
     */
    void onVerified(VerifyIPRecord verifyIPRecord);

    void onVerify();

    void onInvalidIP();
}
