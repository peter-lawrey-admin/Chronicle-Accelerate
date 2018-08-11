package im.xcl.platform.api;

import im.xcl.platform.dto.Invalidation;
import im.xcl.platform.dto.Verification;
import net.openhft.chronicle.bytes.MethodId;

public interface Verifier {
    /**
     * Called when a connection is first made
     */
    void onConnection();

    /**
     * Send a verification message which has been signed
     */
    @MethodId(1)
    void verification(Verification verification);

    /**
     * Notify that a server was invalidated
     */
    @MethodId(2)
    void invalidation(Invalidation record);
}
