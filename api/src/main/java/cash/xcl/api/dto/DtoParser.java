package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;

import java.util.function.BiConsumer;

import static cash.xcl.api.dto.MethodIds.*;

public class DtoParser {
    final AddressInformationEvent aie = new AddressInformationEvent();
    final CreateNewAddressCommand cnac = new CreateNewAddressCommand();
    final ExchangeRateEvent ere = new ExchangeRateEvent();
    final NewAddressRejectedEvent nare = new NewAddressRejectedEvent();
    final OpeningBalanceEvent obe = new OpeningBalanceEvent();
    final SubscriptionCommand sc = new SubscriptionCommand();
    final TransactionBlockEvent tbe = new TransactionBlockEvent();
    final TransferValueCommand tvc = new TransferValueCommand();
    final TreeBlockEvent treebe = new TreeBlockEvent();

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

        switch (messageType) {
            case ADDRESS_INFORMATION_EVENT:
                parse(bytes, aie, messages, AllMessages::addressInformationEvent);
                break;

            case CREATE_NEW_ADDRESS_COMMAND:
                parse(bytes, cnac, messages, AllMessages::createNewAddressCommand);
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

            case TRANSACTION_BLOCK_EVENT:
                parse(bytes, tbe, messages, AllMessages::transactionBlockEvent);
                break;

            case TRANSFER_VALUE_COMMAND:
                parse(bytes, tvc, messages, AllMessages::transferValueCommand);
                break;

            case TREE_BLOCK_EVENT:
                parse(bytes, treebe, messages, AllMessages::treeBlockEvent);
                break;

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
