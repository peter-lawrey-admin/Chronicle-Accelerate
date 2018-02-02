package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;


public class WithdrawValueInformationEvent extends SignedMessage {
    // TODO
    //private WithdrawValueCommand wvc;
    private Transfer transfer = new Transfer();

    public WithdrawValueInformationEvent(long sourceAddress, long eventTime, Transfer transfer) {
        super(sourceAddress, eventTime);
        this.transfer = transfer;
    }


    public WithdrawValueInformationEvent() {
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
        return MethodIds.WITHDRAW_VALUE_INFORMATION_EVENT;
    }


    public Transfer transfer() {
        return transfer;
    }

    public WithdrawValueInformationEvent transfer(Transfer transfer) {
        this.transfer = transfer;
        return this;
    }
}
