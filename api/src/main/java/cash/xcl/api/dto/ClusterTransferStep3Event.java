package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferStep3Event extends SignedMessage {

    private ClusterTransferStep3Command clusterTransferStep3Command = new ClusterTransferStep3Command();

    public ClusterTransferStep3Event(long sourceAddress, long eventTime, ClusterTransferStep3Command clusterTransferStep3Command) {
        super(sourceAddress, eventTime);
        this.clusterTransferStep3Command = clusterTransferStep3Command;
    }

    public ClusterTransferStep3Event() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        clusterTransferStep3Command = ((Bytes<?>) bytes).readMarshallableLength16(ClusterTransferStep3Command.class, clusterTransferStep3Command);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeMarshallableLength16(clusterTransferStep3Command);
    }

    @Override
    public int messageType() {
        return MessageTypes.CLUSTER_TRANSFER_STEP3_EVENT;
    }

    public ClusterTransferStep3Command clusterTransferStep3Command() {
        return clusterTransferStep3Command;
    }

    public ClusterTransferStep3Event clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
        this.clusterTransferStep3Command = clusterTransferStep3Command;
        return this;
    }
}
