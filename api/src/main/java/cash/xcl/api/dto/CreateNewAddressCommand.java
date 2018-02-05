package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.salt.Ed25519;

public class CreateNewAddressCommand extends SignedMessage {
    private Bytes publicKey;
    private String region;

    public CreateNewAddressCommand(long sourceAddress, long eventTime, Bytes publicKey, String region) {
        super(sourceAddress, eventTime);
        this.publicKey = publicKey;
        this.region = region;
    }

    public CreateNewAddressCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        if (publicKey == null) publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.clear();
        bytes.read(publicKey, Ed25519.PUBLIC_KEY_LENGTH);

        region = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.write(publicKey);

        bytes.writeUtf8(region);
    }

    @Override
    public int messageType() {
        return MethodIds.CREATE_NEW_ADDRESS_COMMAND;
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public CreateNewAddressCommand publicKey(Bytes publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String region() {
        return region;
    }

    public CreateNewAddressCommand region(String region) {
        this.region = region;
        return this;
    }
}
