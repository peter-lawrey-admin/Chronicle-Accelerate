package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class ClusterTransferStep2Command extends SignedMessage {

    private ClusterTransferStep1Command clusterTransferStep1Command = new ClusterTransferStep1Command();

    public ClusterTransferStep2Command(long sourceAddress, long eventTime, ClusterTransferStep1Command clusterTransferStep1Command) {
        super(sourceAddress, eventTime);
        this.clusterTransferStep1Command = clusterTransferStep1Command;
    }

    public ClusterTransferStep2Command() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        clusterTransferStep1Command = ((Bytes<?>) bytes).readMarshallableLength16(ClusterTransferStep1Command.class, clusterTransferStep1Command);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeMarshallableLength16(clusterTransferStep1Command);
    }

    @Override
    public int messageType() {
        return MessageTypes.CLUSTER_TRANSFER_STEP2_COMMAND;
    }

    public ClusterTransferStep1Command clusterTransferStep1Command() {
        return clusterTransferStep1Command;
    }

    public ClusterTransferStep2Command clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        this.clusterTransferStep1Command = clusterTransferStep1Command;
        return this;
    }
}
