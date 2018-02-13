package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class DepositValueEvent extends SignedMessage {

    private DepositValueCommand depositValueCommand;

    public DepositValueEvent(long sourceAddress, long eventTime, DepositValueCommand depositValueCommand) {
        super(sourceAddress, eventTime);
        this.depositValueCommand = depositValueCommand;
    }

    public DepositValueEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        depositValueCommand = ((Bytes<?>) bytes).readMarshallableLength16(DepositValueCommand.class, depositValueCommand);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeMarshallableLength16(depositValueCommand);
    }

    @Override
    public int messageType() {
        return MessageTypes.DEPOSIT_VALUE_EVENT;
    }

    public DepositValueCommand depositValueCommand() {
        return depositValueCommand;
    }

    public DepositValueEvent depositValueCommand(DepositValueCommand depositValueCommand) {
        this.depositValueCommand = depositValueCommand;
        return this;
    }
}
