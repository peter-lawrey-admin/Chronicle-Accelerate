package cash.xcl.server;

import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;

public interface Voter extends ServerComponent {
    void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent);

    void sendVote(long blockNumber);
}
