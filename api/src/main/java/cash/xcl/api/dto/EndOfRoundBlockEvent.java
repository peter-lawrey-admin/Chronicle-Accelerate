package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class EndOfRoundBlockEvent extends SignedMessage {
    private String region;
    private int weekNumber;
    private long blockNumber;
    private Map<Long, Long> blockRecords = new LinkedHashMap<>();

    public EndOfRoundBlockEvent(long sourceAddress, long eventTime, String region, int weekNumber, long blockNumber, Map<Long, Long> blockRecords) {
        super(sourceAddress, eventTime);
        this.region = region;
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
        this.blockRecords = blockRecords;
    }

    public EndOfRoundBlockEvent() {

    }

    @NotNull
    @Override
    public <T> T deepCopy() {
        EndOfRoundBlockEvent tbe = new EndOfRoundBlockEvent();
        this.copyTo(tbe);
        return (T) tbe;
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        EndOfRoundBlockEvent tbe = (EndOfRoundBlockEvent) t;
        super.copyTo(t);
        tbe.region(region);
        tbe.weekNumber(weekNumber);
        tbe.blockNumber(blockNumber);
        assert !blockRecords.containsKey(0L);
        tbe.blockRecords().putAll(blockRecords);
        return t;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readUtf8();
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        blockRecords.clear();
        int blocks = (int) bytes.readStopBit();
        for (int i = 0; i < blocks; i++)
            blockRecords.put(bytes.readLong(), bytes.readLong());
//        System.out.println("Read "+this);
        assert !blockRecords.containsKey(0L);
    }


    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        assert !blockRecords.containsKey(0L);
//        System.out.println("Write "+this);
        bytes.writeUtf8(region);
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeStopBit(blockRecords.size());
        for (Map.Entry<Long, Long> entry : blockRecords.entrySet()) {
            bytes.writeLong(entry.getKey()).writeLong(entry.getValue());
        }
    }

    @Override
    public int messageType() {
        return MessageTypes.TREE_BLOCK_EVENT;
    }

    public String region() {
        return region;
    }

    public EndOfRoundBlockEvent region(String region) {
        this.region = region;
        return this;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public EndOfRoundBlockEvent weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public EndOfRoundBlockEvent blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }

    public Map<Long, Long> blockRecords() {
        if (blockRecords == null)
            blockRecords = new LinkedHashMap<>();
        return blockRecords;
    }
}

