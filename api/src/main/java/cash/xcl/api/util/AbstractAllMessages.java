package cash.xcl.api.util;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.ApplicationMessageEvent;
import cash.xcl.api.dto.BlockSubscriptionQuery;
import cash.xcl.api.dto.ClusterStatusQuery;
import cash.xcl.api.dto.ClusterStatusResponse;
import cash.xcl.api.dto.ClusterTransferStep1Command;
import cash.xcl.api.dto.ClusterTransferStep2Command;
import cash.xcl.api.dto.ClusterTransferStep3Command;
import cash.xcl.api.dto.ClusterTransferStep3Event;
import cash.xcl.api.dto.ClustersStatusQuery;
import cash.xcl.api.dto.ClustersStatusResponse;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.CurrentBalanceResponse;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.DepositValueEvent;
import cash.xcl.api.dto.EndOfRoundBlockEvent;
import cash.xcl.api.dto.ExchangeRateEvent;
import cash.xcl.api.dto.ExchangeRateQuery;
import cash.xcl.api.dto.ExchangeRateResponse;
import cash.xcl.api.dto.ExecutionReportEvent;
import cash.xcl.api.dto.FeesEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;
import cash.xcl.api.dto.QueryFailedResponse;
import cash.xcl.api.dto.ServiceNodesEvent;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.dto.SubscriptionSuccessResponse;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;
import cash.xcl.api.dto.TransactionBlockVoteEvent;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.dto.TransferValueEvent;
import cash.xcl.api.dto.WithdrawValueCommand;
import cash.xcl.api.dto.WithdrawValueEvent;
import cash.xcl.api.exch.CancelOrderCommand;
import cash.xcl.api.exch.NewLimitOrderCommand;

public class AbstractAllMessages implements AllMessagesServer {
    protected final long address;
    private AllMessagesLookup lookup;

    public AbstractAllMessages(long address) {
        this.address = address;
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
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
    public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
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
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        throw new UnsupportedOperationException(getClass().getName());
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
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
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
