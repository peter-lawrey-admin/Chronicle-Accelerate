package cash.xcl.api;

import cash.xcl.api.dto.SubscriptionCommand;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TreeBlockEvent;

public interface ServerIn {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void subscriptionCommand(SubscriptionCommand subscriptionCommand);
}
