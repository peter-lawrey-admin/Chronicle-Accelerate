package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedBinaryMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.util.Validators.notNull;

public class DepositValueEvent extends SignedBinaryMessage {

    private DepositValueCommand depositValueCommand;

    public DepositValueEvent(long sourceAddress, long eventTime, DepositValueCommand depositValueCommand) {
        super(sourceAddress, eventTime);
        this.depositValueCommand = notNull(depositValueCommand);
    }


    public DepositValueEvent init(long sourceAddress, long eventTime, DepositValueCommand depositValueCommand) {
        super.init(sourceAddress, eventTime);
        this.depositValueCommand = notNull(depositValueCommand);
        return this;
    }


    public DepositValueEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        if (depositValueCommand == null) {
            depositValueCommand = new DepositValueCommand();
        }
        depositValueCommand.readMarshallable(bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        if (depositValueCommand == null) {
            throw new NullPointerException();
        }
        depositValueCommand.writeMarshallable(bytes);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.DEPOSIT_VALUE_EVENT;
    }

    public DepositValueCommand depositValueCommand() {
        return depositValueCommand;
    }

    public DepositValueEvent depositValueCommand(DepositValueCommand depositValueCommand) {
        this.depositValueCommand = notNull(depositValueCommand);
        return this;
    }
}
