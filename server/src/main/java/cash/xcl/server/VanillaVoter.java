package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class VanillaVoter implements Voter {
    static final Long NO_BLOCK = -1L;
    private final Map<Long, Long> lastBlockMap = new LinkedHashMap<>();
    private final Map<Long, Long> lastVoteMap;
    private final long[] clusterAddresses;
    private final TransactionBlockGossipEvent vote;
    private AllMessagesLookup lookup;

    public VanillaVoter(String region, long[] clusterAddresses) {
        this.clusterAddresses = clusterAddresses;
        lastVoteMap = new LinkedHashMap<>();
        vote = new TransactionBlockGossipEvent(0, 0, region, 0, 0, lastVoteMap);
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void close() {

    }

    @Override
    public synchronized void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        Long sourceAddress = transactionBlockEvent.sourceAddress();
        if (sourceAddress == 0) {
            System.err.println("Missing sourceAddress " + transactionBlockEvent);
            return;
        }
        long lastBlockNumber = lastBlockMap.getOrDefault(sourceAddress, NO_BLOCK);
        long blockNumber = transactionBlockEvent.blockNumber();
        if (lastBlockNumber < blockNumber)
            lastBlockMap.put(sourceAddress, blockNumber);
    }

    @Override
    public void sendVote(long blockNumber) {
        vote.reset();
        vote.blockNumber(blockNumber);
        synchronized (this) {
            lastVoteMap.putAll(lastBlockMap);
        }
        for (long clusterAddress : clusterAddresses) {
            lookup.to(clusterAddress).transactionBlockGossipEvent(vote);
        }
    }
}
