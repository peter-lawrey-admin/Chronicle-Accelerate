package im.xcl.platform.api;

import im.xcl.platform.dto.Invalidation;
import im.xcl.platform.dto.Verification;

public interface Verifier {
    /**
     * Called when a connection is first made
     */
    void onConnection();

    /**
     * Send a verification message which has been signed
     */
    void verification(Verification verification);

    /**
     * Notify that a server was invalidated
     */
    void invalidation(Invalidation record);
}
