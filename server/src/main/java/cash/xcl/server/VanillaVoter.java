package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.TransactionBlockGossipEvent;
import cash.xcl.api.dto.TransactionBlockVoteEvent;
import cash.xcl.util.XCLLongLongMap;

public class VanillaVoter implements Voter {
    static final Long NO_BLOCK = -1L;
    private final XCLLongLongMap lastBlockMap = XCLLongLongMap.withExpectedSize(16);
    private final XCLLongLongMap lastVoteMap;
    private final long address;
    private final String region;
    private final long[] clusterAddresses;
    private AllMessagesLookup lookup;
    private TransactionBlockGossipEvent gossip = new TransactionBlockGossipEvent();
    private TransactionBlockVoteEvent vote = new TransactionBlockVoteEvent();

    public VanillaVoter(long address, String region, long[] clusterAddresses) {
        this.address = address;
        this.region = region;
        this.clusterAddresses = clusterAddresses;
        lastVoteMap = XCLLongLongMap.withExpectedSize(16);
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void close() {

    }

    @Override
    public synchronized void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        //System.out.println(address + " " + transactionBlockGossipEvent);
        transactionBlockGossipEvent.copyTo(this.gossip);
    }

    @Override
    public void sendVote(long blockNumber) {
        vote.reset();
        synchronized (this) {
            gossip.copyTo(vote.gossipEvent());
        }
        for (long clusterAddress : clusterAddresses) {
            lookup.to(clusterAddress)
                    .transactionBlockVoteEvent(vote);
        }
    }
}
