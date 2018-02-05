package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferStep1Command extends TransferValueCommand {

    @Override
    protected void readMarshallable2(BytesIn bytes) {
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
    }

    @Override
    public int messageType() {
        return MethodIds.CLUSTER_TRANSFER_STEP1_COMMAND;
    }
}
