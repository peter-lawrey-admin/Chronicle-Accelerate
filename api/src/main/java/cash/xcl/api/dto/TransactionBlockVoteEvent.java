package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class TransactionBlockVoteEvent extends SignedBinaryMessage {
    private TransactionBlockGossipEvent gossipEvent;

    public TransactionBlockVoteEvent(long sourceAddress, long eventTime, TransactionBlockGossipEvent gossipEvent) {
        super(sourceAddress, eventTime);
        this.gossipEvent = gossipEvent;
    }

    public TransactionBlockVoteEvent() {
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        gossipEvent = bytes.readMarshallableLength16(TransactionBlockGossipEvent.class, gossipEvent);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeMarshallableLength16(gossipEvent);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.TRANSACTION_BLOCK_VOTE_EVENT;
    }

    public TransactionBlockGossipEvent gossipEvent() {
        if (gossipEvent == null) gossipEvent = new TransactionBlockGossipEvent();
        return gossipEvent;
    }

    public TransactionBlockVoteEvent gossipEvent(TransactionBlockGossipEvent gossipEvent) {
        this.gossipEvent = gossipEvent;
        return this;
    }

    public int region() {
        return gossipEvent.region();
    }

    public long blockNumber() {
        return gossipEvent.blockNumber();
    }
}
