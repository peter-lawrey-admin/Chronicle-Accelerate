package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferValueCommand extends TransferValueCommand {

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        super.readMarshallable2(bytes);
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        super.writeMarshallable2(bytes);
    }

    @Override
    public int messageType() {
        return MethodIds.CLUSTER_TRANSFER_VALUE_COMMAND;
    }
}
