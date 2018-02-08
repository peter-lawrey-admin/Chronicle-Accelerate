package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

import java.util.LinkedHashMap;
import java.util.Map;

public class TransactionBlockGossipEvent extends SignedMessage {
    private int weekNumber;
    private long blockNumber; // unsigned int
    private Map<Long, Long> addressToBlockNumberMap;

    public TransactionBlockGossipEvent(long sourceAddress, long eventTime, int weekNumber, long blockNumber, Map<Long, Long> addressToBlockNumberMap) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
    }

    public TransactionBlockGossipEvent() {
        addressToBlockNumberMap = new LinkedHashMap<>();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        int entries = (int) bytes.readStopBit();
        if (addressToBlockNumberMap == null) addressToBlockNumberMap = new LinkedHashMap<>();
        for (int i = 0; i < entries; i++)
            addressToBlockNumberMap.put(bytes.readLong(), bytes.readUnsignedInt());
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeStopBit(addressToBlockNumberMap.size());
        for (Map.Entry<Long, Long> entry : addressToBlockNumberMap.entrySet())
            bytes.writeLong(entry.getKey(), entry.getValue());
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
}
