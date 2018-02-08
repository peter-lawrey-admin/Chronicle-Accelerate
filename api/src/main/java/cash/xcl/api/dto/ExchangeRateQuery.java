package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.core.annotation.NotNull;

// FIXME needs reviewing/completing
public class ExchangeRateQuery extends SignedMessage {

    private String symbol1;

    private String symbol2;

    public ExchangeRateQuery(long sourceAddress, long eventTime,
                             @NotNull String symbol1, @NotNull String symbol2) {
        super(sourceAddress, eventTime);
        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
    }

    public ExchangeRateQuery() {
    }

    public String symbol1() {
        return symbol1;
    }

    public ExchangeRateQuery symbol1(String symbol1) {
        this.symbol1 = symbol1;
        return this;
    }

    public String symbol2() {
        return symbol2;
    }

    public ExchangeRateQuery symbol2(String symbol2) {
        this.symbol2 = symbol2;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    public int messageType() {
        return MessageTypes.EXCHANGE_RATE_QUERY;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {

    }
}
