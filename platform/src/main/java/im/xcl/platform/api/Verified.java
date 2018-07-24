package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class Verified extends AbstractBytesMarshallable{
    BytesStore publicKey;

    public Verified(BytesStore publicKey) {
        this.publicKey = publicKey;
    }
}
