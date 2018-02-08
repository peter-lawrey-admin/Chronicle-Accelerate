package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class WithdrawValueCommand extends TransferValueCommand {
    String description;
    String destination;

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        super.readMarshallable2(bytes);
        description = bytes.readUtf8();
        destination = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(description);
        bytes.writeUtf8(destination);
    }

    @Override
    public int messageType() {
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
