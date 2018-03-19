package cash.xcl.api.dto;


import cash.xcl.api.AllMessages;
import cash.xcl.api.exch.*;
import net.openhft.chronicle.bytes.Bytes;

import java.util.function.BiConsumer;

import static cash.xcl.api.dto.MessageTypes.*;

public class DtoParser {
    public static final int PROTOCOL_OFFSET = 80;
    public static final int MESSAGE_OFFSET = 82;

    final TransactionBlockEvent tbe = new TransactionBlockEvent();
    final TransactionBlockGossipEvent tbge = new TransactionBlockGossipEvent();
    final TransactionBlockVoteEvent tbve = new TransactionBlockVoteEvent();
    final EndOfRoundBlockEvent treebe = new EndOfRoundBlockEvent();
    final OpeningBalanceEvent obe = new OpeningBalanceEvent();
    final FeesEvent fe = new FeesEvent();
    final ServiceNodesEvent sne = new ServiceNodesEvent();
    final BlockSubscriptionQuery bsq = new BlockSubscriptionQuery();

    final ApplicationMessageEvent ame = new ApplicationMessageEvent();
    final CommandFailedEvent cfe = new CommandFailedEvent();
    final QueryFailedResponse qfr = new QueryFailedResponse();

    final CreateNewAddressCommand cnac = new CreateNewAddressCommand();
    final CreateNewAddressEvent cnae = new CreateNewAddressEvent();

    final TransferValueCommand tvc = new TransferValueCommand();
    final TransferValueEvent tve = new TransferValueEvent();

    final ClusterTransferStep1Command cts1c = new ClusterTransferStep1Command();
    final ClusterTransferStep2Command cts2c = new ClusterTransferStep2Command();
    final ClusterTransferStep3Command cts3c = new ClusterTransferStep3Command();
    final ClusterTransferStep3Event cts3e = new ClusterTransferStep3Event();

    final TransferFromExchangeCommand tfe = new TransferFromExchangeCommand();
    final TransferToExchangeCommand tte = new TransferToExchangeCommand();

    final DepositValueCommand dvc = new DepositValueCommand();
    final DepositValueEvent dve = new DepositValueEvent();

    final WithdrawValueCommand wvc = new WithdrawValueCommand();
    final WithdrawValueEvent wve = new WithdrawValueEvent();

    final SubscriptionQuery sq = new SubscriptionQuery();

    // Cluster
    final ClusterStatusQuery csq = new ClusterStatusQuery();

    //ClusterS
    final ClustersStatusQuery cSq = new ClustersStatusQuery();

    // CurrentBalance
    final CurrentBalanceQuery cbq = new CurrentBalanceQuery();
    final CurrentBalanceResponse cbe = new CurrentBalanceResponse();

    // ExchangeRate
    final ExchangeRateQuery erq = new ExchangeRateQuery();
    final ExchangeRateEvent ere = new ExchangeRateEvent();

    // Orders
    final NewOrderCommand noc = new NewOrderCommand();
    final CancelOrderCommand coc = new CancelOrderCommand();
    final OrderClosedEvent oce = new OrderClosedEvent();

    // ExecutionReport
    final ExecutionReportEvent execre = new ExecutionReportEvent();

    // Responses
    final SubscriptionSuccessResponse ss = new SubscriptionSuccessResponse();
    final ClusterStatusResponse csr = new ClusterStatusResponse();
    final ClustersStatusResponse cSr = new ClustersStatusResponse();
    final CurrentBalanceResponse cbr = new CurrentBalanceResponse();
    final ExchangeRateResponse err = new ExchangeRateResponse();

    static <T extends SignedMessage, AM> void parse(Bytes<?> bytes, T t, AM am, BiConsumer<AM, T> tConsumer) {
        t.reset();
        bytes.lenient(true);
        t.readMarshallable(bytes);
        tConsumer.accept(am, t);
    }

    public void parseOne(Bytes<?> bytes, AllMessages messages) {
        int protocol = bytes.readUnsignedShort(bytes.readPosition() + PROTOCOL_OFFSET);
        int messageType = bytes.readUnsignedShort(bytes.readPosition() + MESSAGE_OFFSET);

        if (protocol != 1) {
            throw new IllegalArgumentException("protocol: " + protocol);
        }

        switch (messageType) {
        // weekly events
        case TRANSACTION_BLOCK_EVENT:
            parse(bytes, tbe, messages, AllMessages::transactionBlockEvent);
            break;

        case TRANSACTION_BLOCK_GOSSIP_EVENT:
            parse(bytes, tbge, messages, AllMessages::transactionBlockGossipEvent);
            break;

        case TRANSACTION_BLOCK_VOTE_EVENT:
            parse(bytes, tbve, messages, AllMessages::transactionBlockVoteEvent);
            break;

        case TREE_BLOCK_EVENT:
            parse(bytes, treebe, messages, AllMessages::endOfRoundBlockEvent);
            break;

        case OPENING_BALANCE_EVENT:
            parse(bytes, obe, messages, AllMessages::openingBalanceEvent);
            break;

        case FEES_EVENT:
            parse(bytes, fe, messages, AllMessages::feesEvent);
            break;

        case EXCHANGE_RATE_EVENT:
            parse(bytes, ere, messages, AllMessages::exchangeRateEvent);
            break;

        case SERVICE_NODES_EVENT:
            parse(bytes, sne, messages, AllMessages::serviceNodesEvent);
            break;

        case BLOCK_SUBSCRIPTION_QUERY:
            parse(bytes, bsq, messages, AllMessages::blockSubscriptionQuery);
            break;

            // runtime events
        case APPLICATION_MESSAGE_EVENT:
            parse(bytes, ame, messages, AllMessages::applicationMessageEvent);
            break;

        case COMMAND_FAILED_EVENT:
            parse(bytes, cfe, messages, AllMessages::commandFailedEvent);
            break;

        case QUERY_FAILED_RESPONSE:
            parse(bytes, qfr, messages, AllMessages::queryFailedResponse);
            break;

            // address
        case CREATE_NEW_ADDRESS_COMMAND:
            parse(bytes, cnac, messages, AllMessages::createNewAddressCommand);
            break;

        case CLUSTER_TRANSFER_STEP1_COMMAND:
            parse(bytes, cts1c, messages, AllMessages::clusterTransferStep1Command);
            break;

        case CLUSTER_TRANSFER_STEP2_COMMAND:
            parse(bytes, cts2c, messages, AllMessages::clusterTransferStep2Command);
            break;

        case CLUSTER_TRANSFER_STEP3_COMMAND:
            parse(bytes, cts3c, messages, AllMessages::clusterTransferStep3Command);
            break;

            // Main chain events
        case CREATE_NEW_ADDRESS_EVENT:
            parse(bytes, cnae, messages, AllMessages::createNewAddressEvent);
            break;

        case CLUSTER_TRANSFER_STEP3_EVENT:
            parse(bytes, cts3e, messages, AllMessages::clusterTransferStep3Event);
            break;

            // transfer value
        case TRANSFER_VALUE_COMMAND:
            parse(bytes, tvc, messages, AllMessages::transferValueCommand);
            break;

        case TRANSFER_FROM_EXCHANGE_COMMAND:
            parse(bytes, tfe, messages, AllMessages::transferFromExchangeCommand);
            break;

        case TRANSFER_TO_EXCHANGE_COMMAND:
            parse(bytes, tte, messages, AllMessages::transferToExchangeCommand);
            break;

        case SUBSCRIPTION_QUERY:
            parse(bytes, sq, messages, AllMessages::subscriptionQuery);
            break;

            // Regional commands and queries
        case TRANSFER_VALUE_EVENT:
            parse(bytes, tve, messages, AllMessages::transferValueEvent);
            break;

            // Responses
        case SUBSCRIPTION_SUCCESS_RESPONSE:
            parse(bytes, ss, messages, AllMessages::subscriptionSuccessResponse);
            break;

        case CURRENT_BALANCE_RESPONSE:
            parse(bytes, cbr, messages, AllMessages::currentBalanceResponse);
            break;

        case EXCHANGE_RATE_RESPONSE:
            parse(bytes, err, messages, AllMessages::exchangeRateResponse);
            break;

        case CLUSTER_STATUS_RESPONSE:
            parse(bytes, csr, messages, AllMessages::clusterStatusResponse);
            break;

        case CLUSTERS_STATUS_RESPONSE:
            parse(bytes, cSr, messages, AllMessages::clustersStatusResponse);
            break;

            // deposit value
        case DEPOSIT_VALUE_COMMAND:
            parse(bytes, dvc, messages, AllMessages::depositValueCommand);
            break;

        case DEPOSIT_VALUE_EVENT:
            parse(bytes, dve, messages, AllMessages::depositValueEvent);
            break;

            // Withdraw Value
        case WITHDRAW_VALUE_COMMAND:
            parse(bytes, wvc, messages, AllMessages::withdrawValueCommand);
            break;

        case WITHDRAW_VALUE_EVENT:
            parse(bytes, wve, messages, AllMessages::withdrawValueEvent);
            break;

            // Cluster
        case CLUSTER_STATUS_QUERY:
            parse(bytes, csq, messages, AllMessages::clusterStatusQuery);
            break;

            //ClusterS
        case CLUSTERS_STATUS_QUERY:
            parse(bytes, cSq, messages, AllMessages::clustersStatusQuery);
            break;

        case CURRENT_BALANCE_QUERY:
            parse(bytes, cbq, messages, AllMessages::currentBalanceQuery);
            break;

            // ExchangeRate
        case EXCHANGE_RATE_QUERY:
            parse(bytes, erq, messages, AllMessages::exchangeRateQuery);
            break;

        case NEW_ORDER_COMMAND:
            parse(bytes, noc, messages, AllMessages::newOrderCommand);
            break;

        case CANCEL_ORDER_COMMAND:
            parse(bytes, coc, messages, AllMessages::cancelOrderCommand);
            break;

        case ORDER_CLOSED_EVENT:
            parse(bytes, oce, messages, AllMessages::orderClosedEvent);
            break;
            // ExecutionReport
        case EXECUTION_REPORT:
            parse(bytes, execre, messages, AllMessages::executionReportEvent);
            break;

        default:
            throw new IllegalArgumentException("Unknown messageType: " + Integer.toHexString(messageType));

        }
    }
}
