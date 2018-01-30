package cash.xcl.api;

import cash.xcl.api.dto.*;
import net.openhft.chronicle.core.io.Closeable;

public interface ServerIn extends Closeable {
    void depositValueCommand(DepositValueCommand depositValueCommand);

    void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand);

    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void subscriptionCommand(SubscriptionCommand subscriptionCommand);
}
