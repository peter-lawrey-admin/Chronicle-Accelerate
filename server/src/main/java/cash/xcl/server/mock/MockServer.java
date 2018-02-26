package cash.xcl.server.mock;

import cash.xcl.api.ServerIn;
import cash.xcl.api.ServerOut;
import cash.xcl.api.dto.BlockSubscriptionQuery;
import cash.xcl.api.dto.ClusterTransferStep1Command;
import cash.xcl.api.dto.ClusterTransferStep2Command;
import cash.xcl.api.dto.ClusterTransferStep3Command;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.CurrentBalanceResponse;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.EndOfRoundBlockEvent;
import cash.xcl.api.dto.ExchangeRateEvent;
import cash.xcl.api.dto.FeesEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;
import cash.xcl.api.dto.ServiceNodesEvent;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.dto.TransactionBlockGossipEvent;
import cash.xcl.api.dto.TransactionBlockVoteEvent;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.dto.WithdrawValueCommand;
import cash.xcl.api.exch.CancelOrderCommand;
import cash.xcl.api.exch.NewLimitOrderCommand;
import cash.xcl.api.exch.TransferFromExchangeCommand;
import cash.xcl.api.exch.TransferToExchangeCommand;
import cash.xcl.api.util.XCLBase32;
import cash.xcl.server.AddressService;
import net.openhft.chronicle.bytes.Bytes;

public class MockServer implements ServerIn {
    private final AddressService addressService = new AddressService();

    private ServerOut serverOut;
    private long sourceAddress = 0;

    public MockServer(ServerOut serverOut) {
        this.serverOut = serverOut;
    }

    public long sourceAddress() {
        return sourceAddress;
    }

    public MockServer sourceAddress(long sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }

    public long eventTime() {
        return 0; // for reproducibility;
    }

    public MockServer serverOut(ServerOut serverOut) {
        this.serverOut = serverOut;
        return this;
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        String region = XCLBase32.normalize(createNewAddressCommand.region());
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
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
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
