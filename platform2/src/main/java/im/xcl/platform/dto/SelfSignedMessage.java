package im.xcl.platform.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.salt.Ed25519;

/**
 * This message states this node verifies a given public key after connecting to it successfully.
 */
public class SelfSignedMessage<M extends SelfSignedMessage<M>> extends VanillaSignedMessage<M> {
    private final Bytes publicKey = Bytes.allocateElasticDirect(Ed25519.PUBLIC_KEY_LENGTH);

    public SelfSignedMessage(int protocol, int messageType) {
        super(protocol, messageType);
    }

    @Override
    public boolean hasPublicKey() {
        return true;
    }

    @Override
    public M publicKey(BytesStore key) {
        assert !signed();
        long offset = key.readLimit() - Ed25519.PUBLIC_KEY_LENGTH;
        this.publicKey.clear().write(key, offset, Ed25519.PUBLIC_KEY_LENGTH);
        return (M) this;
    }

    public BytesStore publicKey() {
        return publicKey;
    }
}
