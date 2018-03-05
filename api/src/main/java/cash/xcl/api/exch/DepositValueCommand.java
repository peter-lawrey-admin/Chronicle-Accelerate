package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.TransferValueCommand;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class DepositValueCommand extends TransferValueCommand {
    String description;

    public DepositValueCommand() {
        super();
    }

    public DepositValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, String currency, String reference,
                               String description) {
        super(sourceAddress, eventTime, toAddress, amount, currency, reference);
        this.description = description;
    }

    public DepositValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, String currency) {
        super(sourceAddress, eventTime, toAddress, amount, currency, null);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        super.readMarshallable2(bytes);
        description = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(description);
    }

    @Override
    public int messageType() {
        return MessageTypes.DEPOSIT_VALUE_COMMAND;
    }

    public String description() {
        return description;
    }

    public DepositValueCommand description(String description) {
        this.description = description;
        return this;
    }
}
