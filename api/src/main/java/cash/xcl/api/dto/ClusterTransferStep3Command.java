package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferStep3Command extends SignedMessage {

    private ClusterTransferStep2Command clusterTransferStep2Command = new ClusterTransferStep2Command();

    public ClusterTransferStep3Command(long sourceAddress, long eventTime, ClusterTransferStep2Command clusterTransferStep2Command) {
        super(sourceAddress, eventTime);
        this.clusterTransferStep2Command = clusterTransferStep2Command;
    }


    public ClusterTransferStep3Command() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        clusterTransferStep2Command = ((Bytes<?>) bytes).readMarshallableLength16(ClusterTransferStep2Command.class, clusterTransferStep2Command);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeMarshallableLength16(clusterTransferStep2Command);
    }

    @Override
    public int messageType() {
        return MethodIds.CLUSTER_TRANSFER_STEP3_COMMAND;
    }

    public ClusterTransferStep2Command clusterTransferStep2Command() {
        return clusterTransferStep2Command;
    }

    public ClusterTransferStep3Command clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
        this.clusterTransferStep2Command = clusterTransferStep2Command;
        return this;
    }
}
