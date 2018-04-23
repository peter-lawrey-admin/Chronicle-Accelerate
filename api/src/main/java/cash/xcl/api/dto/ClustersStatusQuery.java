package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
// This message is a query for all the known clusters and
// the services they provide.
public class ClustersStatusQuery extends SignedBinaryMessage {
    public ClustersStatusQuery(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    public ClustersStatusQuery() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {

    }

    @Override
    public int intMessageType() {
        return MessageTypes.CLUSTERS_STATUS_QUERY;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {

    }
}
