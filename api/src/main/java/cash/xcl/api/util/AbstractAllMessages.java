package cash.xcl.api.util;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.*;
import cash.xcl.api.exch.*;
import cash.xcl.api.exch.Side;
import cash.xcl.util.XCLBase32LongConverter;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.wire.LongConversion;

import java.util.HashSet;
import java.util.Set;


public class AbstractAllMessages implements AllMessagesServer {
    @LongConversion(XCLBase32LongConverter.class)
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
    public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
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

//    @Override
//    public void newOrderCommand(NewOrderCommand newOrderCommand) {
//        throw new UnsupportedOperationException(getClass().getName());
//    }

    //TODO remove asap
    protected Set<Long> orders = new HashSet<>();

    // TODO remove this method asap. It's only used for initial development/testing of the Fix Gateway
    @Override
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        long eventTime = SystemTimeProvider.INSTANCE.currentTimeMicros();
        long sourceAddress = newOrderCommand.sourceAddress();
        try {
            orders.add(newOrderCommand.eventTime());
            cash.xcl.api.exch.ExecutionReport er = new cash.xcl.api.exch.ExecutionReport(newOrderCommand.getCurrencyPair(), Side.BUY, 1.0, 1.0, 1L, 2L);
            ExecutionReportEvent ere = new ExecutionReportEvent(this.address, eventTime, er);
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.executionReportEvent(ere);
        } catch (Exception e) {
            e.printStackTrace();
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, newOrderCommand, e.toString());
            AllMessages allMessages = to(sourceAddress);
            allMessages.commandFailedEvent(cfe);
        }
    }


//    @Override
//    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
//        throw new UnsupportedOperationException(getClass().getName());
//    }
    // TODO remove this method asap. It's only used for initial development/testing of the Fix Gateway
    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        long eventTime = SystemTimeProvider.INSTANCE.currentTimeMicros();
        long sourceAddress = cancelOrderCommand.sourceAddress();
        try {
            if( orders.contains(cancelOrderCommand.eventTime()) ) {
                cash.xcl.api.exch.OrderClosedEvent ore = new cash.xcl.api.exch.OrderClosedEvent(
                        this.address,
                        eventTime,
                        cancelOrderCommand.sourceAddress(),
                        cancelOrderCommand.eventTime(),
                        OrderClosedEvent.REASON.USER_REQUEST);
                AllMessages messageWriter = to(sourceAddress);
                messageWriter.orderClosedEvent(ore);
                orders.remove(cancelOrderCommand.eventTime());
            } else {
                throw new Exception("could not find order to cancel - orderId: " + cancelOrderCommand.eventTime() );
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, cancelOrderCommand, e.toString());
            AllMessages allMessages = to(sourceAddress);
            allMessages.commandFailedEvent(cfe);
        }
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
    public void transferToExchangeCommand(TransferToExchangeCommand transferCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transferFromExchangeCommand(TransferFromExchangeCommand transferCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException(getClass().getName());
    }


}
