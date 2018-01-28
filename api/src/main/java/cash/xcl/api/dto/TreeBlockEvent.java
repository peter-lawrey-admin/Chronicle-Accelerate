package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.core.annotation.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TreeBlockEvent extends SignedMessage {
    private int weekNumber;
    private long blockNumber;
    private List<BlockRecord> blockRecordsBuffer = new ArrayList<>();
    private List<BlockRecord> blockRecords = new ArrayList<>();

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        blockRecords.clear();
        while (bytes.readRemaining() >= 16) {
            nextBlockRecord().readMarshallable(bytes);
        }
    }

    @NotNull
    public BlockRecord nextBlockRecord() {
        if (blockRecords.size() == blockRecordsBuffer.size())
            blockRecordsBuffer.add(new BlockRecord());
        BlockRecord br = blockRecordsBuffer.get(blockRecords.size());
        blockRecords.add(br);
        return br;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        for (int i = 0; i < blockRecords.size(); i++)
            blockRecords.get(i).writeMarshallable(bytes);
    }

    @Override
    protected int messageType() {
        return MethodIds.TREE_BLOCK_EVENT;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public TreeBlockEvent weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public TreeBlockEvent blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }

    public TreeBlockEvent clearBlockRecords() {
        this.blockRecords.clear();
        return this;
    }

    public TreeBlockEvent addBlockRecord(BlockRecord br) {
        this.blockRecords.add(br);
        return this;
    }
}

