package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.salt.Ed25519;

public class CreateNewAddressEvent extends SignedTracedMessage {
    private long address;
    private Bytes publicKey;

    public CreateNewAddressEvent(long sourceAddress, long eventTime, SignedMessage orig, long address, Bytes publicKey) {
        super(sourceAddress, eventTime, orig);
        this.address = address;
        this.publicKey = publicKey;
    }

    public CreateNewAddressEvent(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, long address, Bytes publicKey) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime);
        this.address = address;
        this.publicKey = publicKey;
    }

    public CreateNewAddressEvent() {
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        super.readMarshallable2(bytes);
        if (publicKey == null) publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.clear();
        bytes.read(publicKey, Ed25519.PUBLIC_KEY_LENGTH);
        address = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        super.writeMarshallable2(bytes);
        bytes.write(publicKey);
        bytes.writeLong(address);
    }

    @Override
    public int messageType() {
        return MessageTypes.CREATE_NEW_ADDRESS_EVENT;
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public CreateNewAddressEvent publicKey(Bytes publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public long address() {
        return address;
    }

    public CreateNewAddressEvent address(long address) {
        this.address = address;
        return this;
    }
}
