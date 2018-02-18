package cash.xcl.server;

import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.TransactionBlockEvent;

public interface Voter extends ServerComponent {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void sendVote(long blockNumber);
}
