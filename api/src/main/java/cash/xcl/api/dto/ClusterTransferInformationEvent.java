package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferInformationEvent extends SignedMessage {

    // TODO
    //private ClusterTransferValueCommand tvc;
    private Transfer transfer;

    public ClusterTransferInformationEvent(long sourceAddress, long eventTime, Transfer transfer) {
        super(sourceAddress, eventTime);
        this.transfer = transfer;
    }


    public ClusterTransferInformationEvent() {
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
        return MethodIds.TRANSFER_VALUE_INFORMATION_EVENT;
    }


    public Transfer transfer() {
        return transfer;
    }

    public ClusterTransferInformationEvent transfer(Transfer transfer) {
        this.transfer = transfer;
        return this;
    }
}
