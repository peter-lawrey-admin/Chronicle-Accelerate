package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesStore;

public class TransactionBlockEvent extends SignedMessage {
    private int weekNumber;
    private long blockNumber; // unsigned int

    private Bytes transactions = Bytes.allocateElasticDirect();

    private transient DtoParser dtoParser;

    public TransactionBlockEvent(long sourceAddress, long eventTime, int weekNumber, long blockNumber) {
        super(sourceAddress, eventTime);
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
    }

    public TransactionBlockEvent() {

    }

    public void clear() {
        transactions.clear();
        blockNumber++;
    }

    public void addTransaction(SignedMessage message) {
        message.writeMarshallable(transactions);
    }

    public void replay(AllMessages allMessages) {
        if (dtoParser == null) dtoParser = new DtoParser();
        transactions.readPosition(0);
        while (!transactions.isEmpty())
            dtoParser.parseOne(transactions, allMessages);
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        if (transactions == null) transactions = Bytes.allocateElasticDirect();
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
        return MessageTypes.TRANSACTION_BLOCK_EVENT;
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
