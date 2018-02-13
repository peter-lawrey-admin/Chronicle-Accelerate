package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import cash.xcl.api.tcp.WritingAllMessages;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;

public class TransactionBlockEvent extends SignedMessage {
    private int weekNumber;
    private long blockNumber; // unsigned int

    private transient Bytes transactions = Bytes.allocateElasticDirect();

    private transient int count;
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
        count = 0;
    }

    public void incrementBlockNumber() {
        blockNumber++;
    }

    public void addTransaction(SignedMessage message) {
        count++;
        message.writeMarshallable(transactions);
    }

    public void replay(AllMessages allMessages) {
        if (dtoParser == null) dtoParser = new DtoParser();
        transactions.readPosition(0);
        while (!transactions.isEmpty())
            dtoParser.parseOne(transactions, allMessages);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        if (transactions == null) transactions = Bytes.allocateElasticDirect();
        transactions.clear().write((BytesStore) bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.write(transactions);
    }

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        super.readMarshallable(wire);
        clear();
        wire.read("transactions").sequence(this, (tbe, in) -> {
            while (in.hasNextSequenceItem())
                tbe.addTransaction(in.object(SignedMessage.class));
        });
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        super.writeMarshallable(wire);
        wire.write("transactions").sequence(out -> replay(new WritingAllMessages() {
            @Override
            public AllMessages to(long addressOrRegion) {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void write(SignedMessage message) {
                out.object(message);
            }

            @Override
            public void close() {
                throw new UnsupportedOperationException();
            }
        }));
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

    public int count() {
        return count;
    }
}
