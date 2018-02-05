package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;


public class WithdrawValueEvent extends SignedMessage {
    private WithdrawValueCommand withdrawValueCommand;

    public WithdrawValueEvent(long sourceAddress, long eventTime, WithdrawValueCommand withdrawValueCommand) {
        super(sourceAddress, eventTime);
        this.withdrawValueCommand = withdrawValueCommand;
    }

    public WithdrawValueEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        withdrawValueCommand = ((Bytes<?>) bytes).readMarshallableLength16(WithdrawValueCommand.class, withdrawValueCommand);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeMarshallableLength16(withdrawValueCommand);
    }

    @Override
    public int messageType() {
        return MethodIds.WITHDRAW_VALUE_EVENT;
    }

    public WithdrawValueCommand withdrawValueCommand() {
        return withdrawValueCommand;
    }

    public WithdrawValueEvent withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        this.withdrawValueCommand = withdrawValueCommand;
        return this;
    }
}
