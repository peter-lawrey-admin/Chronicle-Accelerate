package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class ClusterTransferStep1Command extends TransferValueCommand {

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
    }

    @Override
    public int messageType() {
        return MessageTypes.CLUSTER_TRANSFER_STEP1_COMMAND;
    }
}
