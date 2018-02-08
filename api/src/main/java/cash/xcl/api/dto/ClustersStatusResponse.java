package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

// FIXME needs reviewing/completing
// This message is a response containing all the known clusters and
// the services they provide.
public class ClustersStatusResponse extends SignedMessage {

    private ClusterStatusQuery clusterStatusQuery;

    // clusters contains the nodes
    // and each node contains its own status
    private Clusters clusters;

    public ClustersStatusResponse(ClusterStatusQuery clusterStatusQuery, Clusters clusters) {
        this.clusterStatusQuery = clusterStatusQuery;
        this.clusters = clusters;
    }

    public ClustersStatusResponse(long sourceAddress, long eventTime, ClusterStatusQuery clusterStatusQuery, Clusters cluster) {
        super(sourceAddress, eventTime);
        this.clusterStatusQuery = clusterStatusQuery;
        this.clusters = cluster;
    }

    public ClustersStatusResponse() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
    }

    @Override
    public int messageType() {
        return MessageTypes.CLUSTERS_STATUS_RESPONSE;
    }
}
