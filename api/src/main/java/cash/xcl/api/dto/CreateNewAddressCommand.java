package cash.xcl.api.dto;

import cash.xcl.util.RegionIntConverter;
import cash.xcl.util.XCLBase32LongConverter;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.IntConversion;
import net.openhft.chronicle.wire.LongConversion;

public class CreateNewAddressCommand extends SignedBinaryMessage {
    private Bytes publicKey;
    @IntConversion(RegionIntConverter.class)
    private int region;
    @LongConversion(XCLBase32LongConverter.class)
    private long newAddressSeed;

    public CreateNewAddressCommand(long sourceAddress, long eventTime, Bytes publicKey, String region) {
        this(sourceAddress, eventTime, publicKey, RegionIntConverter.INSTANCE.parse(region));
    }

    public CreateNewAddressCommand(long sourceAddress, long eventTime, Bytes publicKey, int region) {
        super(sourceAddress, eventTime);
        this.publicKey = publicKey;
        this.region = region;
    }

    public CreateNewAddressCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        if (publicKey == null) publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.clear();
        bytes.read(publicKey, Ed25519.PUBLIC_KEY_LENGTH);

        region = bytes.readInt();
        newAddressSeed = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.write(publicKey);

        bytes.writeInt(region);

        bytes.writeLong(newAddressSeed);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.CREATE_NEW_ADDRESS_COMMAND;
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public CreateNewAddressCommand publicKey(Bytes publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String regionStr() {
        return RegionIntConverter.INSTANCE.asString(region);
    }

    public int region() {
        return region;
    }

    public CreateNewAddressCommand region(int region) {
        this.region = region;
        return this;
    }

    public CreateNewAddressCommand region(String region) {
        this.region = RegionIntConverter.INSTANCE.parse(region);
        return this;
    }

    public long newAddressSeed() {
        return newAddressSeed;
    }

    public CreateNewAddressCommand newAddressSeed(long newAddressSeed) {
        this.newAddressSeed = newAddressSeed;
        return this;
    }
}
