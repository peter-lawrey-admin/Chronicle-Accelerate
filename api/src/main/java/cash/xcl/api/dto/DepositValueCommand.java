package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class DepositValueCommand extends TransferValueCommand {
    String description;

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        super.readMarshallable2(bytes);
        description = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(description);
    }

    @Override
    protected int messageType() {
        return MethodIds.DEPOSIT_VALUE_COMMAND;
    }

    public String description() {
        return description;
    }

    public DepositValueCommand description(String description) {
        this.description = description;
        return this;
    }
}
