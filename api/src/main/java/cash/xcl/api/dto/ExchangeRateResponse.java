package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
public class ExchangeRateResponse extends ExchangeRateEvent {

    private ExchangeRateQuery exchangeRateQuery;

    // TODO: use this instead of inheritance
    //private ExchangeRateEvent exchangeRateEvent;

    public ExchangeRateResponse(long sourceAddress,
                                long eventTime,
                                String symbol1symbol2,
                                double buyPrice,
                                double sellPrice,
                                ExchangeRateQuery exchangeRateQuery) {
        super(sourceAddress, eventTime, symbol1symbol2, buyPrice, sellPrice);
        this.exchangeRateQuery = exchangeRateQuery;
    }

    public ExchangeRateResponse() {
        super();
    }

    public ExchangeRateQuery exchangeRateQuery() {
        return exchangeRateQuery;
    }

    public ExchangeRateResponse exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        this.exchangeRateQuery = exchangeRateQuery;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
    }

    @Override
    public int messageType() {
        return MessageTypes.EXCHANGE_RATE_RESPONSE;
    }
}
