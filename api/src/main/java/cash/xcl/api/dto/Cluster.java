package cash.xcl.api.dto;

import cash.xcl.api.util.CountryRegion;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

import java.util.ArrayList;
import java.util.List;

// FIXME needs reviewing/completing
public class Cluster extends AbstractBytesMarshallable {

    // private String id;
    private List<Node> nodeList;
    private CountryRegion region;

    // TODO
    // services provided by this cluster
    //private Services service;

    // TODO
    //private BlockChain blockChain;
    private BlockRecord blockRecord;

    public Cluster(List<Node> nodeList, CountryRegion region) {
        this.nodeList = nodeList;
        this.region = region;
    }

    public Cluster(CountryRegion region) {
        nodeList = new ArrayList<>();
        this.region = region;
    }

    public Cluster() {
    }

    public void addNode(Node node) {
        nodeList.add(node);
    }

    public boolean contains(Node node) {

        return this.nodeList.contains(node);
    }

    public Cluster nodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
        return this;
    }

    public List<Node> nodeList() {
        return nodeList;
    }

    public CountryRegion region() {
        return region;
    }

    public Cluster region(CountryRegion region) {
        this.region = region;
        return this;
    }

    public BlockRecord blockRecord() {
        return blockRecord;
    }

    public Cluster blockRecord(BlockRecord blockRecord) {
        this.blockRecord = blockRecord;
        return this;
    }

    public int getNumberOfNodes() {

        return nodeList.size();
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {

    }

    @Override
    public void writeMarshallable(BytesOut bytes) {

    }

}
