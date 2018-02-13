package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
public class NewMarketOrderCommand extends SignedMessage {

    private MarketOrder marketOrder;

    public NewMarketOrderCommand(long sourceAddress, long eventTime,
                                 MarketOrder marketOrder) {
        super(sourceAddress, eventTime);
        this.marketOrder = marketOrder;
    }

    public NewMarketOrderCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {

    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {

    }

    @Override
    public int messageType() {

        return MessageTypes.NEW_MARKET_ORDER_COMMAND;
    }

    public NewMarketOrderCommand marketOrder(MarketOrder marketOrder) {
        this.marketOrder = marketOrder;
        return this;
    }
}
