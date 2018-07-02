package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.TransferValueCommand;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class WithdrawValueCommand extends TransferValueCommand {
    private String description;
    private String destination;

    public WithdrawValueCommand() {

    }

    public WithdrawValueCommand(long sourceAddress, long eventTime, long toAddress, double amount, String currency, String reference,
                                String description, String destination) {
        super(sourceAddress, eventTime, toAddress, amount, currency, reference);
        this.description = description;
        this.destination = destination;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        super.readMarshallable2(bytes);
        description = bytes.readUtf8();
        destination = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(description);
        bytes.writeUtf8(destination);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.WITHDRAW_VALUE_COMMAND;
    }

    public String description() {
        return description;
    }

    public WithdrawValueCommand description(String description) {
        this.description = description;
        return this;
    }

    public String destination() {
        return destination;
    }

    public WithdrawValueCommand destination(String destination) {
        this.destination = destination;
        return this;
    }
}
