package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class DepositValueRejectedEvent extends SignedMessage {
    DepositValueCommand command;
    int reasonCode;
    String reason;

    public DepositValueRejectedEvent(long sourceAddress, long eventTime, DepositValueCommand command, int reasonCode, String reason) {
        super(sourceAddress, eventTime);
        this.command = command;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public DepositValueRejectedEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        BytesIn<?> in = bytes;
        command = in.readMarshallableLength16(DepositValueCommand.class, command);
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
    public int messageType() {
        return MethodIds.DEPOSIT_VALUE_REJECTED_EVENT;
    }

    public DepositValueCommand command() {
        return command;
    }

    public DepositValueRejectedEvent command(DepositValueCommand command) {
        this.command = command;
        return this;
    }

    public int reasonCode() {
        return reasonCode;
    }

    public DepositValueRejectedEvent reasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String reason() {
        return reason;
    }

    public DepositValueRejectedEvent reason(String reason) {
        this.reason = reason;
        return this;
    }
}
