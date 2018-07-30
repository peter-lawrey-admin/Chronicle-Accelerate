package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;

public interface MessageRouter<T> {
    T to(BytesStore publicKey);
}
