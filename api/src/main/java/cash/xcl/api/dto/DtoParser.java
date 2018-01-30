package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;

import java.util.function.BiConsumer;

import static cash.xcl.api.dto.MethodIds.*;

public class DtoParser {
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
        t.readMarshallable(bytes);
        tConsumer.accept(am, t);
    }

    public void parseOne(Bytes bytes, AllMessages messages) {
        int protocol = bytes.readUnsignedByte(bytes.readPosition() + 80);
        int messageType = bytes.readUnsignedByte(bytes.readPosition() + 81);

        if (protocol != 1)
            throw new IllegalArgumentException("protocol: " + protocol);

        /*
            int TRANSACTION_BLOCK_EVENT = 0x01;
    int TREE_BLOCK_EVENT = 0x02;
    int APPLICATION_MESSAGE_EVENT = 0x03;
    int CREATE_NEW_ADDRESS_COMMAND = 0x20;
    int CLUSTER_TRANSFER_VALUE_COMMAND = 0x21;
    int ADDRESS_INFORMATION_EVENT = 0x30;
    int EXCHANGE_RATE_EVENT = 0x31;
    int NEW_ADDRESS_REJECTED_EVENT = 0x32;
    int OPENING_BALANCE_EVENT = 0x50;
    int TRANSFER_VALUE_COMMAND = 0x51;
    int DEPOSIT_VALUE_COMMAND = 0x60;
    int WITHDRAW_VALUE_COMMAND = 0x61;
    int NEW_MARKET_ORDER = 0x62;
    int NEW_LIMIT_ORDER = 0x63;
    int CANCEL_ORDER = 0x64;
    int EXECUTION_REPORT = 0x70;

    int SUBSCRIPTION_COMMAND = 0xF0;
         */
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
                throw new IllegalArgumentException("Not implemented messageType: " + messageType);

            case SUBSCRIPTION_COMMAND:
                parse(bytes, sc, messages, AllMessages::subscriptionCommand);

            default:
                throw new IllegalArgumentException("Unknown messageType: " + messageType);
//            case NEW_MARKET_ORDER:
//            case NEW_LIMIT_ORDER:
//            case CANCEL_ORDER:
//            case EXECUTION_REPORT:
        }
    }
}
