package cash.xcl.api.dto;

import cash.xcl.util.RegionIntConverter;
import cash.xcl.util.XCLLongLongMap;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.IntConversion;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

public class TransactionBlockGossipEvent extends SignedBinaryMessage {
    @IntConversion(RegionIntConverter.class)
    private int region;
    private int weekNumber;
    private long blockNumber; // unsigned int
    private XCLLongLongMap addressToBlockNumberMap;
    private LongU32Writer longU32Writer = new LongU32Writer();

    public TransactionBlockGossipEvent(long sourceAddress, long eventTime, int region, int weekNumber, long blockNumber, XCLLongLongMap addressToBlockNumberMap) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
        this.region = region;
        this.addressToBlockNumberMap = addressToBlockNumberMap;
    }

    public TransactionBlockGossipEvent() {
        addressToBlockNumberMap = XCLLongLongMap.withExpectedSize(16);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readInt();
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        int entries = (int) bytes.readStopBit();
        if (addressToBlockNumberMap == null) addressToBlockNumberMap = XCLLongLongMap.withExpectedSize(16);
        for (int i = 0; i < entries; i++)
            addressToBlockNumberMap.put(bytes.readLong(), bytes.readUnsignedInt());
        assert !addressToBlockNumberMap.containsKey(0L);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        assert sourceAddress() != 0;
        assert !addressToBlockNumberMap.containsKey(0L);
        bytes.writeInt(region);
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeStopBit(addressToBlockNumberMap.size());
        longU32Writer.bytes = bytes;
        addressToBlockNumberMap.forEach(longU32Writer);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.TRANSACTION_BLOCK_GOSSIP_EVENT;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public TransactionBlockGossipEvent weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public TransactionBlockGossipEvent blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }

    public XCLLongLongMap addressToBlockNumberMap() {
        return addressToBlockNumberMap;
    }

    public TransactionBlockGossipEvent addressToBlockNumberMap(XCLLongLongMap addressToBlockNumberMap) {
        this.addressToBlockNumberMap = addressToBlockNumberMap;
        return this;
    }

    public TransactionBlockGossipEvent region(int region) {
        this.region = region;
        return this;
    }

    public int region() {
        return region;
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        super.copyTo(t);
        TransactionBlockGossipEvent tbge = (TransactionBlockGossipEvent) t;
        tbge.weekNumber(weekNumber)
                .blockNumber(blockNumber)
                .region(region);
        XCLLongLongMap map = tbge.addressToBlockNumberMap();
        map.putAll(addressToBlockNumberMap);
        return t;
    }

}
