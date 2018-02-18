package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.TransactionBlockVoteEvent;
import cash.xcl.api.dto.TreeBlockEvent;

import java.util.LinkedHashMap;
import java.util.Map;

// TODO only take a majority view rather than last one wins.
// TODO might need a stage before this where the servers announce a proposed TreeNode.

public class VanillaVoteTaker implements VoteTaker {
    private final long address;
    private final long[] clusterAddresses;
    private final String region;
    private AllMessagesLookup lookup;
    private Map<Long, Long> addressToBlockNumberMap = new LinkedHashMap<>();
    private TreeBlockEvent treeBlockEvent = new TreeBlockEvent();

    public VanillaVoteTaker(long address, String region, long[] clusterAddresses) {
        this.address = address;
        this.clusterAddresses = clusterAddresses;
        this.region = region;
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void close() {

    }


    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        System.out.println(address + " " + transactionBlockVoteEvent);
        Map<Long, Long> addressToBlockNumberMap = transactionBlockVoteEvent.gossipEvent().addressToBlockNumberMap();
        assert !addressToBlockNumberMap.containsKey(0L);
        this.addressToBlockNumberMap.putAll(addressToBlockNumberMap);
    }

    @Override
    public void sendTreeNode(long blockNumber) {
        treeBlockEvent.reset();
        treeBlockEvent.sourceAddress(address);
        treeBlockEvent.region(region);
        synchronized (this) {
            assert !addressToBlockNumberMap.containsKey(0L);
            treeBlockEvent.blockRecords().putAll(addressToBlockNumberMap);
        }
        treeBlockEvent.blockNumber(blockNumber);
        for (long clusterAddress : clusterAddresses) {
            lookup.to(clusterAddress)
                    .treeBlockEvent(treeBlockEvent);
        }
    }
}
