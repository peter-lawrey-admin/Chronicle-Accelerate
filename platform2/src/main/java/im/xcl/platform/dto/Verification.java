package im.xcl.platform.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.salt.Ed25519;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public <T> T deepCopy() {
        Verification v2 = new Verification(protocol(), messageType());
        v2.address(address());
        v2.timestampUS(timestampUS());
        v2.publicKey(publicKey());
        v2.keyVerified(keyVerified());
        return (T) v2;
    }
}
