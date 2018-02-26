package cash.xcl.server.mock;

import cash.xcl.api.ClientIn;
import cash.xcl.api.ClientOut;
import cash.xcl.api.ServerOut;
import cash.xcl.api.dto.*;

public class MockGateway implements ClientOut {
    private final MockServer mockServer = new MockServer(null);
    private final ClientIn clientIn;
    private final MockServerOut serverOut;

    public MockGateway(ClientIn clientIn) {
        this.clientIn = clientIn;
        this.serverOut = new MockServerOut();
        mockServer.serverOut(serverOut);
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
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        mockServer.newOrderCommand(newOrderCommand);
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        mockServer.cancelOrderCommand(cancelOrderCommand);
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
        mockServer.close();
    }

    class MockServerOut implements ServerOut {

        @Override
        public void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent) {
            throw new AssertionError(applicationMessageEvent.toString());
        }

        @Override
        public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
            clientIn.createNewAddressEvent(createNewAddressEvent);
        }

        @Override
        public void commandFailedEvent(CommandFailedEvent commandFailedEvent) {
            clientIn.commandFailedEvent(commandFailedEvent);
        }

        @Override
        public void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command) {
            //            clientIn.clusterTransferStep2Command(clusterTransferStep2Command);
        }

        @Override
        public void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command) {
            //            clientIn.clusterTransferStep3Command(clusterTransferStep3Command);
        }

        @Override
        public void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event) {
            clientIn.clusterTransferStep3Event(clusterTransferStep3Event);
        }

        @Override
        public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
            clientIn.executionReportEvent(executionReportEvent);
        }

        @Override
        public void close() {

        }
    }

}
