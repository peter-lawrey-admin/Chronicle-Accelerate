package cash.xcl.api;

import cash.xcl.api.dto.*;
import cash.xcl.net.TCPClientListener;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.VanillaTCPClient;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class XCLClient implements AllMessages, Closeable {
    final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);
    private final VanillaTCPClient tcpClient;
    private final long address;
    private final Bytes secretKey;
    private final AllMessages allMessageListener;

    public XCLClient(String name,
                     List<InetSocketAddress> socketAddresses,
                     long address,
                     Bytes secretKey,
                     AllMessages allMessageListener) {
        this.address = address;
        this.secretKey = secretKey;
        this.allMessageListener = allMessageListener;
        this.tcpClient = new VanillaTCPClient(name, socketAddresses, new ClientListener());
    }

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
    public void treeBlockEvent(TreeBlockEvent treeBlockEvent) {
        write(treeBlockEvent);
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
    public void newMarketOrderCommand(NewMarketOrderCommand newMarketOrderCommand) {
        write(newMarketOrderCommand);
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        write(cancelOrderCommand);
    }

    @Override
    public void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent) {
        write(serviceNodesEvent);
    }

    private void write(SignedMessage message) {
        try {
            if (!message.hasSignature()) {
                Bytes bytes = bytesTL.get();
                bytes.clear();
                message.sign(bytes, address, secretKey);
            }
            tcpClient.write(message.sigAndMsg());

        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void close() {
        tcpClient.close();
    }

    class ClientListener implements TCPClientListener {
        final DtoParser parser = new DtoParser();

        @Override
        public void onMessage(TCPConnection client, Bytes bytes) throws IOException {
            try {
                parser.parseOne(bytes, allMessageListener);

            } catch (IORuntimeException iore) {
                if (iore.getCause() instanceof IOException)
                    throw (IOException) iore.getCause();
                throw iore;
            }
        }
    }
}
