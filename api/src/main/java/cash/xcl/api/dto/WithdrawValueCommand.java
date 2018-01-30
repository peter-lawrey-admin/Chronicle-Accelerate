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
    protected int messageType() {
        return MethodIds.WITHDRAW_VALUE_COMMAND;
    }
}
