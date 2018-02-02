package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class DepositValueInformationEvent extends SignedMessage {

    // TODO
    //private DepositValueCommand tvc;
    private Transfer transfer = new Transfer();

    public DepositValueInformationEvent(long sourceAddress, long eventTime, Transfer transfer) {
        super(sourceAddress, eventTime);
        this.transfer = transfer;
    }


    public DepositValueInformationEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        transfer.doReadMarshallable(bytes);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        transfer.doWriteMarshallable(bytes);
    }

    @Override
    public int messageType() {
        return MethodIds.DEPOSIT_INFORMATION_EVENT;
    }


    public Transfer transfer() {
        return transfer;
    }

    public DepositValueInformationEvent transfer(Transfer transfer) {
        this.transfer = transfer;
        return this;
    }
}
