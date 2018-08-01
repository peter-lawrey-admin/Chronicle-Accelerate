package town.lost.examples.verifyip.api;

import im.xcl.platform.api.TransactionListener;
import net.openhft.chronicle.bytes.MethodId;

public interface VerifyIPResponse extends TransactionListener {
    /**
     * Notify that this node has been verified
     *
     * @param verify recording that this node has been verified
     */
    @MethodId(~0x90)
    void onVerify(Verify verify);

    @MethodId(~0x91)
    void onInvalidIP();
}
