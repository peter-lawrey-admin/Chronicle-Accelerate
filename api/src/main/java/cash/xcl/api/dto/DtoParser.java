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
    final ClusterTransferValueCommand ctvc = new ClusterTransferValueCommand();
    final AddressInformationEvent aie = new AddressInformationEvent();
    final ExchangeRateEvent ere = new ExchangeRateEvent();
    final NewAddressRejectedEvent nare = new NewAddressRejectedEvent();
    final OpeningBalanceEvent obe = new OpeningBalanceEvent();
    final TransferValueCommand tvc = new TransferValueCommand();
    final DepositValueCommand dvc = new DepositValueCommand();
    final WithdrawValueCommand wvc = new WithdrawValueCommand();

    final SubscriptionCommand sc = new SubscriptionCommand();

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

            case CREATE_NEW_ADDRESS_COMMAND:
                parse(bytes, cnac, messages, AllMessages::createNewAddressCommand);
                break;

            case CLUSTER_TRANSFER_VALUE_COMMAND:
                parse(bytes, ctvc, messages, AllMessages::clusterTransferValueCommand);
                break;

            case ADDRESS_INFORMATION_EVENT:
                parse(bytes, aie, messages, AllMessages::addressInformationEvent);
                break;

            case EXCHANGE_RATE_EVENT:
                parse(bytes, ere, messages, AllMessages::exchangeRateEvent);
                break;

            case NEW_ADDRESS_REJECTED_EVENT:
                parse(bytes, nare, messages, AllMessages::newAddressRejectedEvent);
                break;

            case OPENING_BALANCE_EVENT:
                parse(bytes, obe, messages, AllMessages::openingBalanceEvent);
                break;

            case TRANSFER_VALUE_COMMAND:
                parse(bytes, tvc, messages, AllMessages::transferValueCommand);
                break;

            case DEPOSIT_VALUE_COMMAND:
                parse(bytes, dvc, messages, AllMessages::depositValueCommand);
                break;

            case WITHDRAW_VALUE_COMMAND:
                parse(bytes, wvc, messages, AllMessages::withdrawValueCommand);
                break;

            case NEW_MARKET_ORDER:
            case NEW_LIMIT_ORDER:
            case CANCEL_ORDER:
                throw new IllegalArgumentException("Not implemented messageType: " + Integer.toHexString(messageType));

            case SUBSCRIPTION_COMMAND:
                parse(bytes, sc, messages, AllMessages::subscriptionCommand);

            default:
                throw new IllegalArgumentException("Unknown messageType: " + Integer.toHexString(messageType));
//            case NEW_MARKET_ORDER:
//            case NEW_LIMIT_ORDER:
//            case CANCEL_ORDER:
//            case EXECUTION_REPORT:
        }
    }
}
