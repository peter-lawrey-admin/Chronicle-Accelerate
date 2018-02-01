package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class TransferValueRejectedEvent extends SignedMessage {
    TransferValueCommand command;
    int reasonCode;
    String reason;

    public TransferValueRejectedEvent(long sourceAddress, long eventTime, TransferValueCommand command, int reasonCode, String reason) {
        super(sourceAddress, eventTime);
        this.command = command;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public TransferValueRejectedEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        BytesIn<?> in = bytes;
        command = in.readMarshallableLength16(TransferValueCommand.class, command);
        command.readMarshallable(bytes);
        reasonCode = bytes.readInt();
        reason = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        command.writeMarshallable(bytes);
        bytes.writeInt(reasonCode);
        bytes.writeUtf8(reason);
    }

    @Override
    protected int messageType() {
        return MethodIds.TRANSFER_VALUE_REJECTED_EVENT;
    }

    public TransferValueCommand command() {
        return command;
    }

    public TransferValueRejectedEvent command(TransferValueCommand command) {
        this.command = command;
        return this;
    }

    public int reasonCode() {
        return reasonCode;
    }

    public TransferValueRejectedEvent reasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String reason() {
        return reason;
    }

    public TransferValueRejectedEvent reason(String reason) {
        this.reason = reason;
        return this;
    }
}
