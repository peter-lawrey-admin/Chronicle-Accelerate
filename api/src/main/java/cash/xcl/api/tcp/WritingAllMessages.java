package cash.xcl.api.tcp;

import cash.xcl.api.AllMessages;
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
import cash.xcl.api.dto.SignedMessage;
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

public abstract class WritingAllMessages implements AllMessages {
    @Override
    public abstract AllMessages to(long addressOrRegion);

    @Override
    public void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent) {
        write(applicationMessageEvent);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        write(createNewAddressCommand);
    }

    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        write(clusterTransferStep1Command);
    }

    @Override
    public void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
        write(clusterTransferStep2Command);
    }

    @Override
    public void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
        write(clusterTransferStep3Command);
    }

    @Override
    public void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event) {
        write(clusterTransferStep3Event);
    }

    @Override
    public void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        write(transactionBlockGossipEvent);
    }

    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        write(transactionBlockVoteEvent);
    }

    @Override
    public void feesEvent(FeesEvent feesEvent) {
        write(feesEvent);
    }

    @Override
    public void blockSubscriptionQuery(BlockSubscriptionQuery blockSubscriptionQuery) {
        write(blockSubscriptionQuery);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        write(transferValueCommand);
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        write(transactionBlockEvent);
    }

    @Override
    public void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
        write(endOfRoundBlockEvent);
    }

    @Override
    public void transferValueEvent(TransferValueEvent transferValueEvent) {
        write(transferValueEvent);
    }

    @Override
    public void depositValueEvent(DepositValueEvent depositValueEvent) {
        write(depositValueEvent);
    }

    @Override
    public void withdrawValueEvent(WithdrawValueEvent withdrawValueEvent) {
        write(withdrawValueEvent);
    }

    @Override
    public void subscriptionSuccessResponse(SubscriptionSuccessResponse subscriptionSuccessResponse) {
        write(subscriptionSuccessResponse);
    }

    @Override
    public void clusterStatusResponse(ClusterStatusResponse clusterStatusResponse) {
        write(clusterStatusResponse);
    }

    @Override
    public void clustersStatusResponse(ClustersStatusResponse clustersStatusResponse) {
        write(clustersStatusResponse);
    }

    @Override
    public void currentBalanceResponse(CurrentBalanceResponse currentBalanceResponse) {
        write(currentBalanceResponse);
    }

    @Override
    public void exchangeRateResponse(ExchangeRateResponse exchangeRateResponse) {
        write(exchangeRateResponse);
    }

    @Override
    public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
        write(createNewAddressEvent);
    }

    @Override
    public void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent) {
        write(exchangeRateEvent);
    }

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        write(openingBalanceEvent);
    }

    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        write(depositValueCommand);
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        write(withdrawValueCommand);
    }

    @Override
    public void queryFailedResponse(QueryFailedResponse queryFailedResponse) {
        write(queryFailedResponse);
    }

    @Override
    public void commandFailedEvent(CommandFailedEvent commandFailedEvent) {
        write(commandFailedEvent);
    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        write(subscriptionQuery);
    }

    @Override
    public void clusterStatusQuery(ClusterStatusQuery clusterStatusQuery) {
        write(clusterStatusQuery);
    }

    @Override
    public void clustersStatusQuery(ClustersStatusQuery clustersStatusQuery) {
        write(clustersStatusQuery);
    }

    @Override
    public void currentBalanceEvent(CurrentBalanceResponse currentBalanceResponse) {
        write(currentBalanceResponse);
    }

    @Override
    public void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        write(currentBalanceQuery);
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        write(exchangeRateQuery);
    }

    @Override
    public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
        write(executionReportEvent);
    }

    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        write(newLimitOrderCommand);
    }


    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        write(cancelOrderCommand);
    }

    @Override
    public void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent) {
        write(serviceNodesEvent);
    }

    protected abstract void write(SignedMessage message);
}
