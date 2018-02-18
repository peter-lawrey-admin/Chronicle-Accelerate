package cash.xcl.server;

import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;

public interface VoteTaker extends ServerComponent {
    void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent);

    void sendTreeNode(long blockNumber);
}
