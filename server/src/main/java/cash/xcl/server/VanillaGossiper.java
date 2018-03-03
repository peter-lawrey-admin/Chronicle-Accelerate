package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;
import cash.xcl.util.XCLLongLongMap;

public class VanillaGossiper implements Gossiper {
    private static final Long NO_BLOCK = -1L;
    private final long address;
    private final XCLLongLongMap lastBlockMap = XCLLongLongMap.withExpectedSize(16);
    private final XCLLongLongMap lastVoteMap;
    private final long[] clusterAddresses;
    private final TransactionBlockGossipEvent gossip;
    private AllMessagesLookup lookup;

    public VanillaGossiper(long address, String region, long[] clusterAddresses) {
        this.address = address;
        this.clusterAddresses = clusterAddresses;
        lastVoteMap = XCLLongLongMap.withExpectedSize(16);
        gossip = new TransactionBlockGossipEvent(0, 0, region, 0, 0, lastVoteMap);
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
        //System.out.println(address + " " + transactionBlockEvent);
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
    public void sendGossip(long blockNumber) {
        gossip.reset();
        gossip.sourceAddress(address);
        gossip.blockNumber(blockNumber);
        synchronized (this) {
            lastVoteMap.putAll(lastBlockMap);
        }
        for (long clusterAddress : clusterAddresses) {
            lookup.to(clusterAddress).transactionBlockGossipEvent(gossip);
        }
    }
}
