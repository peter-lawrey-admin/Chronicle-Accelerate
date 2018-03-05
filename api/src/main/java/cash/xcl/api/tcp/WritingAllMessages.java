package cash.xcl.api.tcp;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.*;
import cash.xcl.api.exch.*;

public abstract class WritingAllMessages implements AllMessages {
    @Override
    public abstract WritingAllMessages to(long addressOrRegion);

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
    public void orderClosedEvent(OrderClosedEvent orderClosedEvent) {
        write(orderClosedEvent);
    }

    @Override
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        write(newOrderCommand);
    }


    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        write(cancelOrderCommand);
    }

    @Override
    public void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent) {
        write(serviceNodesEvent);
    }

    @Override
    public void transferToExchangeCommand(TransferToExchangeCommand transferCommand) {
        write(transferCommand);
    }

    @Override
    public void transferFromExchangeCommand(TransferFromExchangeCommand transferCommand) {
        write(transferCommand);
    }

    public abstract void write(SignedMessage message);

    public void write(long address, SignedMessage message) {
        to(address).write(message);
    }
}
