package cash.xcl.api.util;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.*;
import cash.xcl.api.exch.OrderClosedEvent;

public class AbstractAllMessages implements AllMessagesServer {
    protected long address;
    protected AllMessagesLookup lookup;

    public AbstractAllMessages(long address) {
        this.address = address;
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        if (addressOrRegion == address) {
            return this;
        }
        return lookup.to(addressOrRegion);
    }

    @Override
    public void commandFailedEvent(CommandFailedEvent commandFailedEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void queryFailedResponse(QueryFailedResponse queryFailedResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transferValueEvent(TransferValueEvent transferValueEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void depositValueEvent(DepositValueEvent depositValueEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void withdrawValueEvent(WithdrawValueEvent withdrawValueEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void executionReportEvent(cash.xcl.api.dto.ExecutionReportEvent executionReportEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void orderClosedEvent(OrderClosedEvent orderClosedEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void subscriptionSuccessResponse(SubscriptionSuccessResponse subscriptionSuccessResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clusterStatusResponse(ClusterStatusResponse clusterStatusResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clustersStatusResponse(ClustersStatusResponse clustersStatusResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void currentBalanceResponse(CurrentBalanceResponse currentBalanceResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void exchangeRateResponse(ExchangeRateResponse exchangeRateResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        // todo any further validation required?
    }

    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clusterStatusQuery(ClusterStatusQuery clusterStatusQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clustersStatusQuery(ClustersStatusQuery clustersStatusQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void blockSubscriptionQuery(BlockSubscriptionQuery blockSubscriptionQuery) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void feesEvent(FeesEvent feesEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void currentBalanceEvent(CurrentBalanceResponse currentBalanceResponse) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(getClass().getName());
    }

}
