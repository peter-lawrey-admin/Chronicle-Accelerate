package cash.xcl.api.dto;


import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.annotation.NotNull;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

// FIXME needs reviewing/completing
public class Node extends AbstractBytesMarshallable {

    private String ipAddress;

    // account
    private String accountAddress;
    // aren't the node's balance and stake the same thing?
    private double balance;
    private double stake;

    private NodeStatus nodeStatus;

    private Cluster parentCluster;



    public Node(@NotNull String ipAddress,
                @NotNull String accountAddress,
                double balance,
                double stake,
                @NotNull NodeStatus nodeStatus,
                @NotNull Cluster parentCluster) {
        this.ipAddress = ipAddress;
        this.accountAddress = accountAddress;
        this.balance = balance;
        this.stake = stake;
        this.nodeStatus = nodeStatus;
        this.parentCluster = parentCluster;
    }




    public String ipAddress() {
        return ipAddress;
    }

    public Node ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String accountAddress() {
        return accountAddress;
    }

    public Node accountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
        return this;
    }

    public double balance() {
        return balance;
    }

    public Node balance(double balance) {
        this.balance = balance;
        return this;
    }

    public double stake() {
        return stake;
    }

    public Node stake(double stake) {
        this.stake = stake;
        return this;
    }

    public NodeStatus nodeStatus() {
        return nodeStatus;
    }

    public Node nodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
        return this;
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {


    }

    @Override
    public void writeMarshallable(BytesOut bytes) {


    }

}
