package cash.xcl.api.dto;

import org.jetbrains.annotations.NotNull;

import cash.xcl.api.AllMessages;
import cash.xcl.api.tcp.WritingAllMessages;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;

public class TransactionBlockEvent extends SignedMessage {
    private String region;
    private int weekNumber;
    private long blockNumber; // unsigned int

    private transient Bytes transactions = Bytes.allocateElasticDirect();

    private transient int count;
    private transient DtoParser dtoParser;

    public TransactionBlockEvent(long sourceAddress, long eventTime, String region, int weekNumber, long blockNumber) {
        super(sourceAddress, eventTime);
        this.region = region;
        this.weekNumber = weekNumber;
        this.blockNumber = blockNumber;
    }

    public TransactionBlockEvent() {

    }

    @Override
    public void reset() {
        super.reset();
        transactions.clear();
        count = 0;
    }

    public TransactionBlockEvent addTransaction(SignedMessage message) {
        count++;
        message.writeMarshallable(transactions);
        return this;
    }

    public void replay(AllMessages allMessages) {
        if (dtoParser == null) {
            dtoParser = new DtoParser();
        }
        transactions.readPosition(0);
        while (!transactions.isEmpty()) {
            dtoParser.parseOne(transactions, allMessages);
        }
        transactions.readPosition(0);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readUtf8();
        weekNumber = bytes.readUnsignedShort();
        blockNumber = bytes.readUnsignedInt();
        if (transactions == null) {
            transactions = Bytes.allocateElasticDirect();
        }
        transactions.clear().write((BytesStore) bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        //        System.out.println("Write " + this);
        bytes.writeUtf8(region);
        bytes.writeUnsignedShort(weekNumber);
        bytes.writeUnsignedInt(blockNumber);
        bytes.write(transactions);
    }

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        reset();
        super.readMarshallable(wire);
        wire.read("transactions").sequence(this, (tbe, in) -> {
            while (in.hasNextSequenceItem()) {
                tbe.addTransaction(in.object(SignedMessage.class));
            }
        });
        //        System.out.println("Read " + this);
    }


    @NotNull
    @Override
    public <T> T deepCopy() {
        TransactionBlockEvent tbe = new TransactionBlockEvent();
        this.copyTo(tbe);
        return (T) tbe;
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        TransactionBlockEvent tbe = (TransactionBlockEvent) t;
        super.copyTo(t);
        tbe.region(region);
        tbe.weekNumber(weekNumber);
        tbe.blockNumber(blockNumber);
        tbe.transactions()
                .clear()
                .write(transactions);
        return t;
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

    public String region() {
        return region;
    }

    public TransactionBlockEvent region(String region) {
        this.region = region;
        return this;
    }

    public int count() {
        return count;
    }
}
