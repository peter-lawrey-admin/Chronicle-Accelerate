package cash.xcl.api.dto;

import cash.xcl.util.XCLBase32;
import cash.xcl.util.XCLBase32LongConverter;
import cash.xcl.util.XCLBase32UpperIntConverter;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.IntConversion;
import net.openhft.chronicle.wire.LongConversion;

public class TransferValueCommand extends SignedBinaryMessage {
    @LongConversion(XCLBase32LongConverter.class)
    long toAddress;

    double amount;
    @IntConversion(XCLBase32UpperIntConverter.class)
    int currency;

    String reference;

    public TransferValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, String currency, String reference) {
        this(sourceAddress, eventTime, toAddress, amount, XCLBase32.decodeInt(currency), reference);
    }

    public TransferValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, int currency, String reference) {
        super(sourceAddress, eventTime);
        this.toAddress = toAddress;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
    }

    public TransferValueCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        toAddress = bytes.readLong();
        amount = bytes.readDouble();
        currency = bytes.readInt();
        reference = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(toAddress);
        bytes.writeDouble(amount);
        bytes.writeInt(currency);
        bytes.writeUtf8(reference);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.TRANSFER_VALUE_COMMAND;
    }

    public long toAddress() {
        return toAddress;
    }

    public TransferValueCommand toAddress(long toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public double amount() {
        return amount;
    }

    public TransferValueCommand amount(double amount) {
        this.amount = amount;
        return this;
    }

    public String currencyStr() {
        return XCLBase32.encodeIntUpper(currency);
    }

    public int currency() {
        return currency;
    }

    public TransferValueCommand currency(int currency) {
        this.currency = currency;
        return this;
    }

    public String reference() {
        return reference;
    }

    public TransferValueCommand reference(String reference) {
        this.reference = reference;
        return this;
    }
}
