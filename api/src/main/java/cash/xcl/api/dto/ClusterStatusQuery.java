package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
// queries the status of the nodes in the current cluster
public class ClusterStatusQuery extends SignedBinaryMessage {
    public ClusterStatusQuery(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    public ClusterStatusQuery() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {

    }

    @Override
    public int intMessageType() {
        return MessageTypes.CLUSTER_STATUS_QUERY;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {

    }
}
