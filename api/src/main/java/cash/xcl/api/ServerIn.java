package cash.xcl.api;

import cash.xcl.api.dto.*;
import net.openhft.chronicle.core.io.Closeable;

public interface ServerIn extends Closeable {
    void depositValueCommand(DepositValueCommand depositValueCommand);

    void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand);

    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void treeBlockEvent(TreeBlockEvent treeBlockEvent);

    void subscriptionQuery(SubscriptionQuery subscriptionQuery);


    void clusterTransferStep1Command(ClusterTransferStep2Command clusterTransferStep1Command);

    void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command);

    void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command);


}
