package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.salt.Ed25519;

public class CreateAccount extends AbstractSignedMessage<CreateAccount> {
    private final Bytes publicKey = Bytes.allocateElasticDirect(Ed25519.PUBLIC_KEY_LENGTH);

    public CreateAccount(int protocol, int messageType) {
        super(protocol, messageType);
    }

    @Override
    public BytesStore publicKey() {
        return publicKey;
    }

    @Override
    public CreateAccount publicKey(BytesStore key) {
        assert !signed();
        long offset = key.readLimit() - Ed25519.PUBLIC_KEY_LENGTH;
        this.publicKey.clear().write(key, offset, Ed25519.PUBLIC_KEY_LENGTH);
        return this;
    }

    @Override
    public boolean hasPublicKey() {
        return true;
    }
}
