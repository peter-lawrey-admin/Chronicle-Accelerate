package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionBlockGossipEvent extends SignedMessage {
    private String region;
    private int weekNumber;
    private long blockNumber; // unsigned int
    private Map<Long, Long> addressToBlockNumberMap;

    public TransactionBlockGossipEvent(long sourceAddress, long eventTime, String region, int weekNumber, long blockNumber, Map<Long, Long> addressToBlockNumberMap) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
        this.region = region;
        this.addressToBlockNumberMap = addressToBlockNumberMap;
    }

    public TransactionBlockGossipEvent() {
        addressToBlockNumberMap = new LinkedHashMap<>();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readUtf8();
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        int entries = (int) bytes.readStopBit();
        if (addressToBlockNumberMap == null) addressToBlockNumberMap = new LinkedHashMap<>();
        for (int i = 0; i < entries; i++)
            addressToBlockNumberMap.put(bytes.readLong(), bytes.readUnsignedInt());
        assert !addressToBlockNumberMap.containsKey(0L);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        assert sourceAddress() != 0;
        assert !addressToBlockNumberMap.containsKey(0L);
        bytes.writeUtf8(region);
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeStopBit(addressToBlockNumberMap.size());
        for (Map.Entry<Long, Long> entry : addressToBlockNumberMap.entrySet())
            bytes.writeLong(entry.getKey()).writeUnsignedInt(entry.getValue());
    }

    @Override
    public int messageType() {
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

    public Map<Long, Long> addressToBlockNumberMap() {
        return addressToBlockNumberMap;
    }

    public TransactionBlockGossipEvent addressToBlockNumberMap(Map<Long, Long> addressToBlockNumberMap) {
        this.addressToBlockNumberMap = addressToBlockNumberMap;
        return this;
    }

    public TransactionBlockGossipEvent region(String region) {
        this.region = region;
        return this;
    }

    public String region() {
        return region;
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        super.copyTo(t);
        TransactionBlockGossipEvent tbge = (TransactionBlockGossipEvent) t;
        tbge.weekNumber(weekNumber)
                .blockNumber(blockNumber)
                .region(region);
        Map<Long, Long> map = tbge.addressToBlockNumberMap();
        map.putAll(addressToBlockNumberMap);
        return t;
    }
}
