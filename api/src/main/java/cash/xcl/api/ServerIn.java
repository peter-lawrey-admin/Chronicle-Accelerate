package cash.xcl.api;

import cash.xcl.api.dto.SubscriptionCommand;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TreeBlockEvent;
import net.openhft.chronicle.core.io.Closeable;

public interface ServerIn extends Closeable {
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void subscriptionCommand(SubscriptionCommand subscriptionCommand);
}
