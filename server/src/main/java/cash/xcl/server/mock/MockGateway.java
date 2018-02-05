package cash.xcl.server.mock;

import cash.xcl.api.ClientIn;
import cash.xcl.api.ClientOut;
import cash.xcl.api.ServerOut;
import cash.xcl.api.dto.*;

public class MockGateway implements ClientOut, ServerOut {
    private final MockServer mockServer = new MockServer(null);
    private final ClientIn clientIn;

    public MockGateway(ClientIn clientIn) {
        this.clientIn = clientIn;
        mockServer.serverOut(this);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        mockServer.createNewAddressCommand(createNewAddressCommand);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        mockServer.transferValueCommand(transferValueCommand);
    }

    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        mockServer.clusterTransferStep1Command(clusterTransferStep1Command);
    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        // todo needs validation of the address subscribed.
        clientIn.subscriptionSuccessResponse(new SubscriptionSuccessResponse(0, 0, subscriptionQuery));
    }

    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        mockServer.newLimitOrderCommand(newLimitOrderCommand);
    }

    @Override
    public void newMarketOrderCommand(NewMarketOrderCommand newMarketOrderCommand) {
        mockServer.newMarketOrderCommand(newMarketOrderCommand);
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        mockServer.cancelOrderCommand(cancelOrderCommand);
    }

    @Override
    public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
        clientIn.createNewAddressEvent(createNewAddressEvent);
    }

    @Override
    public void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent) {
        throw new AssertionError(applicationMessageEvent.toString());
    }

    @Override
    public void commandFailedEvent(CommandFailedEvent commandFailedEvent) {
        clientIn.commandFailedEvent(commandFailedEvent);
    }

    @Override
    public void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
        mockServer.clusterTransferStep2Command(clusterTransferStep2Command);
    }

    @Override
    public void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
        mockServer.clusterTransferStep3Command(clusterTransferStep3Command);
    }

    @Override
    public void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event) {
        clientIn.clusterTransferStep3Event(clusterTransferStep3Event);
    }

    @Override
    public void close() {
        mockServer.close();
    }
}
