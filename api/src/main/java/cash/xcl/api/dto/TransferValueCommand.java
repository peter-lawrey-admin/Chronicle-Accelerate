package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class TransferValueCommand extends SignedMessage {
    long toAddress;
    double amount;
    String currency;
    String reference;

    public TransferValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, String currency, String reference) {
        super(sourceAddress, eventTime);
        this.toAddress = toAddress;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
    }

    public TransferValueCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        toAddress = bytes.readLong();
        amount = bytes.readDouble();
        currency = bytes.readUtf8();
        reference = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeLong(toAddress);
        bytes.writeDouble(amount);
        bytes.writeUtf8(currency);
        bytes.writeUtf8(reference);
    }

    @Override
    public int messageType() {
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

    public String currency() {
        return currency;
    }

    public TransferValueCommand currency(String currency) {
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
