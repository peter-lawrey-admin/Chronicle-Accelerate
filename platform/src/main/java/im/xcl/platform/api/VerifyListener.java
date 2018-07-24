package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;

public interface VerifyListener {
    void verified(Verified verified);
}
