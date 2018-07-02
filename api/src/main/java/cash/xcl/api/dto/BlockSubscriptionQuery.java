package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class BlockSubscriptionQuery extends SignedBinaryMessage {
    private int weekNumber;
    private long blockNumber;

    public BlockSubscriptionQuery(long sourceAddress, long eventTime, int weekNumber, long blockNumber) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
    }

    public BlockSubscriptionQuery() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.BLOCK_SUBSCRIPTION_QUERY;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public BlockSubscriptionQuery weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public BlockSubscriptionQuery blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }
}

