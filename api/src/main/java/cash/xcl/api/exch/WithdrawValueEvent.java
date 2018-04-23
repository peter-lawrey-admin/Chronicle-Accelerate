package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedBinaryMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.util.Validators.notNull;

public class WithdrawValueEvent extends SignedBinaryMessage {
    private WithdrawValueCommand withdrawValueCommand;

    public WithdrawValueEvent(long sourceAddress, long eventTime, WithdrawValueCommand withdrawValueCommand) {
        super(sourceAddress, eventTime);
        this.withdrawValueCommand = notNull(withdrawValueCommand);
    }

    public WithdrawValueEvent init(long sourceAddress, long eventTime, WithdrawValueCommand withdrawValueCommand) {
        super.init(sourceAddress, eventTime);
        this.withdrawValueCommand = notNull(withdrawValueCommand);
        return this;
    }



    public WithdrawValueEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        if (withdrawValueCommand == null) {
            withdrawValueCommand = new WithdrawValueCommand();
        }
        withdrawValueCommand.readMarshallable(bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        assert withdrawValueCommand != null;
        withdrawValueCommand.writeMarshallable(bytes);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.WITHDRAW_VALUE_EVENT;
    }

    public WithdrawValueCommand withdrawValueCommand() {
        return withdrawValueCommand;
    }

    public WithdrawValueEvent withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        this.withdrawValueCommand = notNull(withdrawValueCommand);
        return this;
    }
}
