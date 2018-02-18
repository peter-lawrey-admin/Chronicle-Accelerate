package cash.xcl.server;

import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.TransactionBlockEvent;

public interface Gossiper extends ServerComponent {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void sendGossip(long blockNumber);
}
