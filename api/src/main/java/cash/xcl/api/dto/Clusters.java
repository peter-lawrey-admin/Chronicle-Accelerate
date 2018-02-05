package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;


import java.util.List;

// FIXME needs reviewing/completing
public class Clusters extends AbstractBytesMarshallable {

    private static int MAX_NUMBER_OF_NODES = 100;
    private List<Cluster> clusterList;


    public Clusters(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }

    /**
     * when a new node first joins our network we have to register the node in a cluster
     * 
     * @param newNodeEvent
     */
    public void onNewNodeEvent(Node newNodeEvent) {
        // allocate node to cluster
        boolean nodeAdded = false;
        for (Cluster cluster : clusterList) {
            if (cluster.getNumberOfNodes() < MAX_NUMBER_OF_NODES) {
                cluster.addNode(newNodeEvent);
                nodeAdded = true;
                break;
            }
        }
        if (!nodeAdded) {
            Cluster cluster = new Cluster();
            clusterList.add(cluster);
            cluster.addNode(newNodeEvent);
        }
    }

    public Cluster findClusterForAddress(long address) {

        return null;
    }

    public Cluster findClusterForNode(Node node) {
        for (Cluster cluster : clusterList) {
            if (cluster.contains(node)) {
                return cluster;
            }
        }
        return null;
    }

    public boolean isNodeInACluster(Node node) {
        for (Cluster cluster : clusterList) {
            if (cluster.contains(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {

    }

    @Override
    public void writeMarshallable(BytesOut bytes) {

    }


}
