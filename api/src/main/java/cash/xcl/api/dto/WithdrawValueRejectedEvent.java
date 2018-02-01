package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class WithdrawValueRejectedEvent extends SignedMessage {
    WithdrawValueCommand command;
    int reasonCode;
    String reason;

    public WithdrawValueRejectedEvent(long sourceAddress, long eventTime, WithdrawValueCommand command, int reasonCode, String reason) {
        super(sourceAddress, eventTime);
        this.command = command;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public WithdrawValueRejectedEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        BytesIn<?> in = bytes;
        command = in.readMarshallableLength16(WithdrawValueCommand.class, command);
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
        return MethodIds.WITHDRAW_VALUE_REJECTED_EVENT;
    }

    public WithdrawValueCommand command() {
        return command;
    }

    public WithdrawValueRejectedEvent command(WithdrawValueCommand command) {
        this.command = command;
        return this;
    }

    public int reasonCode() {
        return reasonCode;
    }

    public WithdrawValueRejectedEvent reasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String reason() {
        return reason;
    }

    public WithdrawValueRejectedEvent reason(String reason) {
        this.reason = reason;
        return this;
    }
}
