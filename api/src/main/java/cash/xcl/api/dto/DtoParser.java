package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;

import java.util.function.BiConsumer;

import static cash.xcl.api.dto.MethodIds.*;

public class DtoParser {
    public static final int PROTOCOL_OFFSET = 80;
    public static final int MESSAGE_OFFSET = 81;

    final TransactionBlockEvent tbe = new TransactionBlockEvent();
    final TreeBlockEvent treebe = new TreeBlockEvent();
    final ApplicationMessageEvent ame = new ApplicationMessageEvent();

    final CreateNewAddressCommand cnac = new CreateNewAddressCommand();
    final AddressInformationEvent aie = new AddressInformationEvent();
    final NewAddressRejectedEvent nare = new NewAddressRejectedEvent();

    final TransferValueCommand tvc = new TransferValueCommand();
    final TransferInformationEvent tvie = new TransferInformationEvent();
    final TransferValueRejectedEvent tvre = new TransferValueRejectedEvent();

    final ClusterTransferValueCommand ctvc = new ClusterTransferValueCommand();
    final ClusterTransferInformationEvent ctie = new ClusterTransferInformationEvent();
    final ClusterTransferValueRejectedEvent ctvre = new ClusterTransferValueRejectedEvent();

    final DepositValueCommand dvc = new DepositValueCommand();
    final DepositValueInformationEvent dvie = new DepositValueInformationEvent();
    final DepositValueRejectedEvent dvre = new DepositValueRejectedEvent();

    final WithdrawValueCommand wvc = new WithdrawValueCommand();
    final WithdrawValueInformationEvent wvie = new WithdrawValueInformationEvent();
    final WithdrawValueRejectedEvent wvre = new WithdrawValueRejectedEvent();

    final SubscriptionCommand sc = new SubscriptionCommand();
    final SubscriptionSuccess ss = new SubscriptionSuccess();
    final SubscriptionFailed sf = new SubscriptionFailed();

    final ExchangeRateEvent ere = new ExchangeRateEvent();
    final OpeningBalanceEvent obe = new OpeningBalanceEvent();



    static <T extends SignedMessage, AM> void parse(Bytes bytes, T t, AM am, BiConsumer<AM, T> tConsumer) {
        t.reset();
        bytes.lenient(true);
        t.readMarshallable(bytes);
        tConsumer.accept(am, t);
    }

    public void parseOne(Bytes bytes, AllMessages messages) {
        int protocol = bytes.readUnsignedByte(bytes.readPosition() + PROTOCOL_OFFSET);
        int messageType = bytes.readUnsignedByte(bytes.readPosition() + MESSAGE_OFFSET);

        if (protocol != 1)
            throw new IllegalArgumentException("protocol: " + protocol);

        switch (messageType) {
            case TRANSACTION_BLOCK_EVENT:
                parse(bytes, tbe, messages, AllMessages::transactionBlockEvent);
                break;

            case TREE_BLOCK_EVENT:
                parse(bytes, treebe, messages, AllMessages::treeBlockEvent);
                break;

            case APPLICATION_MESSAGE_EVENT:
                parse(bytes, ame, messages, AllMessages::applicationMessageEvent);
                break;

            // address
            case CREATE_NEW_ADDRESS_COMMAND:
                parse(bytes, cnac, messages, AllMessages::createNewAddressCommand);
                break;

            case ADDRESS_INFORMATION_EVENT:
                parse(bytes, aie, messages, AllMessages::addressInformationEvent);
                break;

            case NEW_ADDRESS_REJECTED_EVENT:
                parse(bytes, nare, messages, AllMessages::newAddressRejectedEvent);
                break;

            // transfer value
            case TRANSFER_VALUE_COMMAND:
                parse(bytes, tvc, messages, AllMessages::transferValueCommand);
                break;

            case TRANSFER_VALUE_INFORMATION_EVENT:
                parse(bytes, tvie, messages, AllMessages::transferValueInformationEvent);
                break;

            case TRANSFER_VALUE_REJECTED_EVENT:
                parse(bytes, tvre, messages, AllMessages::transferValueRejectedEvent);
                break;

            // cluster transfer value
            case CLUSTER_TRANSFER_VALUE_COMMAND:
                parse(bytes, ctvc, messages, AllMessages::clusterTransferValueCommand);
                break;

            case CLUSTER_TRANSFER_INFORMATION_EVENT:
                parse(bytes, ctie, messages, AllMessages::clusterTransferInformationEvent);
                break;

            case CLUSTER_TRANSFER_VALUE_REJECTED_EVENT:
                parse(bytes, ctvre, messages, AllMessages::clusterTransferValueRejectedEvent);
                break;

            // deposit value
            case DEPOSIT_VALUE_COMMAND:
                parse(bytes, dvc, messages, AllMessages::depositValueCommand);
                break;

            case DEPOSIT_INFORMATION_EVENT:
                parse(bytes, dvie, messages, AllMessages::depositValueInformationEvent);
                break;

            case DEPOSIT_VALUE_REJECTED_EVENT:
                parse(bytes, dvre, messages, AllMessages::depositValueRejectedEvent);
                break;

            // Withdraw Value
            case WITHDRAW_VALUE_COMMAND:
                parse(bytes, wvc, messages, AllMessages::withdrawValueCommand);
                break;

            case WITHDRAW_VALUE_INFORMATION_EVENT:
                parse(bytes, wvie, messages, AllMessages::withdrawValueInformationEvent);
                break;

            case WITHDRAW_VALUE_REJECTED_EVENT:
                parse(bytes, wvre, messages, AllMessages::withdrawValueRejectedEvent);
                break;

            case SUBSCRIPTION_COMMAND:
                parse(bytes, sc, messages, AllMessages::subscriptionCommand);
                break;

            case SUBSCRIPTION_SUCCESS_EVENT:
                parse(bytes, ss, messages, AllMessages::subscriptionSuccess);
                break;

            case SUBSCRIPTION_FAILED_EVENT:
                parse(bytes, sf, messages, AllMessages::subscriptionFailed);
                break;


            case EXCHANGE_RATE_EVENT:
                parse(bytes, ere, messages, AllMessages::exchangeRateEvent);
                break;

            case OPENING_BALANCE_EVENT:
                parse(bytes, obe, messages, AllMessages::openingBalanceEvent);
                break;

            case NEW_MARKET_ORDER:
            case NEW_LIMIT_ORDER:
            case CANCEL_ORDER:
                throw new IllegalArgumentException("Not implemented messageType: " + Integer.toHexString(messageType));


            default:
                throw new IllegalArgumentException("Unknown messageType: " + Integer.toHexString(messageType));
//            case NEW_MARKET_ORDER:
//            case NEW_LIMIT_ORDER:
//            case CANCEL_ORDER:
//            case EXECUTION_REPORT:
        }
    }
}
