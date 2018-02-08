package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.salt.Ed25519;

public class CreateNewAddressEvent extends SignedMessage {
    private long address;
    private Bytes publicKey;
    // add verifiable facts such as verified address

    public CreateNewAddressEvent(long sourceAddress, long eventTime, long address, Bytes publicKey) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.publicKey = publicKey;
    }

    public CreateNewAddressEvent() {
        super();
    }

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
