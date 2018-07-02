package cash.xcl.server.mock;

import cash.xcl.api.ServerIn;
import cash.xcl.api.ServerOut;
import cash.xcl.api.dto.*;
import cash.xcl.api.exch.*;
import cash.xcl.api.exch.Side;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.server.AddressService;
import net.openhft.chronicle.bytes.Bytes;

import java.util.HashSet;
import java.util.Set;

public class MockServer extends AbstractAllMessages implements ServerIn {
    private final AddressService addressService = new AddressService();
    static public long DEFAULT_SERVER_ADDRESS = 9;

    private ServerOut serverOut;

    public MockServer(ServerOut serverOut) {
        super(DEFAULT_SERVER_ADDRESS);
        this.serverOut = serverOut;
    }

    public long sourceAddress() {
        return address;
    }

    public MockServer sourceAddress(long sourceAddress) {
        this.address = sourceAddress;
        return this;
    }

    public long eventTime() {
        return 0; // for reproducibility;
    }

    public MockServer serverOut(ServerOut serverOut) {
        this.serverOut = serverOut;
        return this;
    }


    protected Set<Long> orders = new HashSet<>();

    @Override
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        long eventTime = newOrderCommand.eventTime();
        try {
            ExecutionReport er = new ExecutionReport(newOrderCommand.getCurrencyPair(), Side.BUY, 1.0, 1.0, 1L, 2L);
            ExecutionReportEvent ere = new ExecutionReportEvent(this.address, eventTime, er);
            serverOut.executionReportEvent(ere);
            orders.add(eventTime);
        } catch (Exception e) {
            CommandFailedEvent cfe = new CommandFailedEvent(this.address, eventTime, newOrderCommand, e.toString());
            serverOut.commandFailedEvent(new CommandFailedEvent(sourceAddress(), eventTime(), cfe, e.getMessage()));
        }
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        long eventTime = cancelOrderCommand.eventTime();
        try {
            if( orders.contains(cancelOrderCommand.eventTime()) ) {
                OrderClosedEvent ore = new OrderClosedEvent(
                        this.address,
                        eventTime,
                        cancelOrderCommand.sourceAddress(),
                        cancelOrderCommand.eventTime(),
                        OrderClosedEvent.REASON.USER_REQUEST);
                serverOut.orderClosedEvent(ore);
                orders.remove(eventTime);
            } else {
                throw new Exception("could not find order to cancel - orderId: " + cancelOrderCommand.eventTime() );
            }
        } catch (Exception e) {
            CommandFailedEvent cfe = new CommandFailedEvent(this.address, eventTime, cancelOrderCommand, e.toString());
            serverOut.commandFailedEvent(new CommandFailedEvent(sourceAddress(), eventTime(), cfe, e.getMessage()));
        }
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        String region = createNewAddressCommand.regionStr();
        try {
            Bytes publicKey = createNewAddressCommand.publicKey();
            long address = addressService.createAddress(region, publicKey);
            serverOut.createNewAddressEvent(
                    new CreateNewAddressEvent(sourceAddress(), eventTime(), createNewAddressCommand, address, publicKey));
        } catch (IllegalArgumentException iae) {
            serverOut.commandFailedEvent(
                    new CommandFailedEvent(sourceAddress(), eventTime(), createNewAddressCommand, iae.getMessage()));
        }
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
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
    public void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
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
    public void blockSubscriptionQuery(BlockSubscriptionQuery blockSubscriptionQuery) {
        throw new UnsupportedOperationException();
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
    public void transferToExchangeCommand(TransferToExchangeCommand transferCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void transferFromExchangeCommand(TransferFromExchangeCommand transferCommand) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    @Override
    public void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

}
