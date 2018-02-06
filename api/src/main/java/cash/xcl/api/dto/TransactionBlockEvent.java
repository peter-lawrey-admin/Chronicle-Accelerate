package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesStore;

public class TransactionBlockEvent extends SignedMessage {
    private final Bytes transactions = Bytes.allocateElasticDirect();
    private int weekNumber;
    private long blockNumber;

    public TransactionBlockEvent(long sourceAddress, long eventTime, int weekNumber, long blockNumber) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
    }

    public TransactionBlockEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        transactions.clear().write((BytesStore) bytes);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.write(transactions);
    }

    @Override
    public int messageType() {
        return MethodIds.TRANSACTION_BLOCK_EVENT;
    }

    // to add helper methods.
    public Bytes transactions() {
        return transactions;
    }

    public int weekNumber() {
        return weekNumber;
    }

    public TransactionBlockEvent weekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public TransactionBlockEvent blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }
}
