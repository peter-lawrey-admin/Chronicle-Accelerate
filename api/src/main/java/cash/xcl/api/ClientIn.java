package cash.xcl.api;

import cash.xcl.api.dto.ClusterStatusResponse;
import cash.xcl.api.dto.ClusterTransferStep3Event;
import cash.xcl.api.dto.ClustersStatusResponse;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.CurrentBalanceResponse;
import cash.xcl.api.dto.DepositValueEvent;
import cash.xcl.api.dto.ExchangeRateResponse;
import cash.xcl.api.dto.QueryFailedResponse;
import cash.xcl.api.dto.SubscriptionSuccessResponse;
import cash.xcl.api.dto.TransferValueEvent;
import cash.xcl.api.dto.WithdrawValueEvent;
import cash.xcl.api.exch.ExecutionReportEvent;
import cash.xcl.api.exch.OrderClosedEvent;

/**
 * Any responses from the Server to the client needed, in addition to ServerOut.
 * <p>
 * Includes all Responses and any events which are expected to go back to a client.
 * In some cases the client may monitor Commands coming from the ServerOut
 */
public interface ClientIn {

    void commandFailedEvent(CommandFailedEvent commandFailedEvent);

    void queryFailedResponse(QueryFailedResponse queryFailedResponse);

    void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent);

    void transferValueEvent(TransferValueEvent transferValueEvent);

    void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event);

    void depositValueEvent(DepositValueEvent depositValueEvent);

    void withdrawValueEvent(WithdrawValueEvent withdrawValueEvent);

    void executionReportEvent(ExecutionReportEvent executionReportEvent);

    void orderClosedEvent(OrderClosedEvent orderClosedEvent);

    // Responses
    void subscriptionSuccessResponse(SubscriptionSuccessResponse subscriptionSuccessResponse);

    void clusterStatusResponse(ClusterStatusResponse clusterStatusResponse);

    void clustersStatusResponse(ClustersStatusResponse clustersStatusResponse);

    void currentBalanceResponse(CurrentBalanceResponse currentBalanceResponse);

    void exchangeRateResponse(ExchangeRateResponse exchangeRateResponse);


}
