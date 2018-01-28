package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.salt.Ed25519;

public class AddressInformationEvent extends SignedMessage {
    private Bytes publicKey;
    private long address;
    // add verifiable facts such as verified address

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        if (publicKey == null) publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.clear();
        bytes.read(publicKey, Ed25519.PUBLIC_KEY_LENGTH);
        address = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.write(publicKey);
        bytes.writeLong(address);
    }

    @Override
    protected int messageType() {
        return MethodIds.ADDRESS_INFORMATION_EVENT;
    }
}
