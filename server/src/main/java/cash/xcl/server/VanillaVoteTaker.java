package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.EndOfRoundBlockEvent;
import cash.xcl.api.dto.TransactionBlockVoteEvent;
import cash.xcl.util.XCLLongLongMap;

// TODO only take a majority view rather than last one wins.
// TODO might need a stage before this where the servers announce a proposed EndOfRoundBlock.

public class VanillaVoteTaker implements VoteTaker {
    private final long address;
    private final long[] clusterAddresses;
    private final String region;
    private AllMessagesLookup lookup;
    private XCLLongLongMap addressToBlockNumberMap = XCLLongLongMap.withExpectedSize(16);
    private EndOfRoundBlockEvent endOfRoundBlockEvent = new EndOfRoundBlockEvent();

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
        //System.out.println(address + " " + transactionBlockVoteEvent);
        XCLLongLongMap addressToBlockNumberMap = transactionBlockVoteEvent.gossipEvent().addressToBlockNumberMap();
        assert !addressToBlockNumberMap.containsKey(0L);
        this.addressToBlockNumberMap.putAll(addressToBlockNumberMap);
    }

    public boolean hasMajority() {
        return true;
    }

    @Override
    public void sendEndOfRoundBlock(long blockNumber) {
        // TODO only do this when a majority of nodes vote the same.
        // TODO see previous method on determining the majority.
        endOfRoundBlockEvent.reset();
        endOfRoundBlockEvent.sourceAddress(address);
        endOfRoundBlockEvent.region(region);
        synchronized (this) {
            assert !addressToBlockNumberMap.containsKey(0L);
            endOfRoundBlockEvent.blockRecords().putAll(addressToBlockNumberMap);
        }
        endOfRoundBlockEvent.blockNumber(blockNumber);
        for (long clusterAddress : clusterAddresses) {
            lookup.to(clusterAddress)
                    .endOfRoundBlockEvent(endOfRoundBlockEvent);
        }
    }
}
