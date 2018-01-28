package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.salt.Ed25519;

public class CreateNewAddressCommand extends SignedMessage {
    private Bytes publicKey;
    private String sourceIP;
    private String region;

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        if (publicKey == null) publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.clear();
        bytes.read(publicKey, Ed25519.PUBLIC_KEY_LENGTH);

        sourceIP = bytes.readUtf8();
        region = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.write(publicKey);

        bytes.writeUtf8(sourceIP);
        bytes.writeUtf8(region);
    }

    @Override
    protected int messageType() {
        return MethodIds.CREATE_NEW_ADDRESS_COMMAND;
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public CreateNewAddressCommand publicKey(Bytes publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String sourceIP() {
        return sourceIP;
    }

    public CreateNewAddressCommand sourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
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
