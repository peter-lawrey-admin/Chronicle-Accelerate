package cash.xcl.api;

import cash.xcl.api.dto.*;
import net.openhft.chronicle.core.io.Closeable;

/**
 * This should be only Commands (no Query/Response) and weekly Events or Events from other clusters.
 */
public interface ServerIn extends WeeklyEvents, Closeable {
    // from client
    void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand);

    void transferValueCommand(TransferValueCommand transferValueCommand);

    void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command);

    // inter cluster query
    void blockSubscriptionQuery(BlockSubscriptionQuery blockSubscriptionQuery);

    // inter cluster commands
    void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command);

    void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command);

    // request to deposit funds from an external gateway
    void depositValueCommand(DepositValueCommand depositValueCommand);

    void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand);

    void newOrderCommand(NewOrderCommand newOrderCommand);

    void cancelOrderCommand(CancelOrderCommand cancelOrderCommand);

}
