package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.ServerComponent;
import cash.xcl.api.dto.*;
import cash.xcl.server.chain.Chainer;

public class RegionalGateway implements AllMessages, ServerComponent {
    public static final String MAIN_CHAIN = "0";
    private final long address;
    private final Chainer localChain = new Chainer("gb", 100, new long[]{1001, 1002, 1003}, this::transactionBlockEvent);
    private final Chainer mainChain = new Chainer(MAIN_CHAIN, 100, new long[]{1001, 1002, 1003}, this::transactionBlockEvent);
    private AllMessagesLookup lookup;

    public RegionalGateway(long address) {
        this.address = address;
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        return lookup.to(addressOrRegion);
    }

    private long address() {
        return address;
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        if (transactionBlockEvent.region().equals(MAIN_CHAIN))
            mainChain.transactionBlockEvent(transactionBlockEvent);
        else
            localChain.transactionBlockEvent(transactionBlockEvent);
    }

    @Override
    public void commandFailedEvent(CommandFailedEvent commandFailedEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryFailedResponse(QueryFailedResponse queryFailedResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void treeBlockEvent(TreeBlockEvent treeBlockEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void feesEvent(FeesEvent feesEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void currentBalanceEvent(CurrentBalanceResponse currentBalanceResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent) {
        System.out.println(address() + ": " + applicationMessageEvent);
    }

    @Override
    public void transferValueEvent(TransferValueEvent transferValueEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void depositValueEvent(DepositValueEvent depositValueEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void withdrawValueEvent(WithdrawValueEvent withdrawValueEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subscriptionSuccessResponse(SubscriptionSuccessResponse subscriptionSuccessResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterStatusResponse(ClusterStatusResponse clusterStatusResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clustersStatusResponse(ClustersStatusResponse clustersStatusResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void currentBalanceResponse(CurrentBalanceResponse currentBalanceResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exchangeRateResponse(ExchangeRateResponse exchangeRateResponse) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        mainChain.createNewAddressCommand(createNewAddressCommand);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void blockSubscriptionQuery(BlockSubscriptionQuery blockSubscriptionQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void newMarketOrderCommand(NewMarketOrderCommand newMarketOrderCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clusterStatusQuery(ClusterStatusQuery clusterStatusQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clustersStatusQuery(ClustersStatusQuery clustersStatusQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        mainChain.close();
        localChain.close();
    }
}
