package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class TransferValueCommand extends SignedMessage {
    long toAddress;
    double amount;
    String currency;

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        toAddress = bytes.readLong();
        amount = bytes.readDouble();
        currency = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeLong(toAddress);
        bytes.writeDouble(amount);
        bytes.writeUtf8(currency);
    }

    @Override
    protected int messageType() {
        return MethodIds.TRANSFER_VALUE_COMMAND;
    }
}
