package cash.xcl.api;

import cash.xcl.api.dto.ClusterStatusQuery;
import cash.xcl.api.dto.ClusterTransferStep1Command;
import cash.xcl.api.dto.ClustersStatusQuery;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.ExchangeRateQuery;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.exch.CancelOrderCommand;
import cash.xcl.api.exch.NewLimitOrderCommand;
import net.openhft.chronicle.core.io.Closeable;

/**
 * Includes all Queries and Commands expected to come from a client.
 */
public interface ClientOut extends Closeable {
    void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand);

    void transferValueCommand(TransferValueCommand transferValueCommand);

    void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command);

    // client only.
    void subscriptionQuery(SubscriptionQuery subscriptionQuery);

    void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand);

    void cancelOrderCommand(CancelOrderCommand cancelOrderCommand);

    void clusterStatusQuery(ClusterStatusQuery clusterStatusQuery);

    void clustersStatusQuery(ClustersStatusQuery clustersStatusQuery);

    void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery);

    void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery);

}
