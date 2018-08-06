package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.salt.Ed25519;

/**
 * This message states this node verifies a given public key after connecting to it successfully.
 */
public class Verification extends SelfSignedMessage<Verification> {
    private final Bytes keyVerified = Bytes.allocateElasticDirect(Ed25519.PUBLIC_KEY_LENGTH);

    public Verification(int protocol, int messageType) {
        super(protocol, messageType);
    }

    public Verification keyVerified(BytesStore key) {
        keyVerified.clear().write(key);
        return this;
    }

    public BytesStore keyVerified() {
        return keyVerified;
    }
}
