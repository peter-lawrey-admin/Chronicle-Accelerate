package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.bytes.NoBytesStore;

public interface MessageRouter<T> {
    BytesStore DEFAULT_CONNECTION = NoBytesStore.noBytesStore();
    T to(BytesStore publicKey);
}
