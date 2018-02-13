package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class ExchangeRateEvent extends SignedMessage {
    private String symbol1, symbol2;
    private double buyPrice, sellPrice;

    public ExchangeRateEvent(long sourceAddress, long eventTime, String symbol1, String symbol2, double buyPrice, double sellPrice) {
        super(sourceAddress, eventTime);
        this.symbol1 = symbol1;
        this.symbol2 = symbol2;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public ExchangeRateEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        symbol1 = bytes.readUtf8();
        symbol2 = bytes.readUtf8();
        buyPrice = bytes.readDouble();
        sellPrice = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUtf8(symbol1);
        bytes.writeUtf8(symbol2);
        bytes.writeDouble(buyPrice);
        bytes.writeDouble(sellPrice);
    }

    @Override
    public int messageType() {
        return MessageTypes.EXCHANGE_RATE_EVENT;
    }

    public String symbol1() {
        return symbol1;
    }

    public ExchangeRateEvent symbol1(String symbol1) {
        this.symbol1 = symbol1;
        return this;
    }

    public String symbol2() {
        return symbol2;
    }

    public ExchangeRateEvent symbol2(String symbol2) {
        this.symbol2 = symbol2;
        return this;
    }

    public double buyPrice() {
        return buyPrice;
    }

    public ExchangeRateEvent buyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
        return this;
    }

    public double sellPrice() {
        return sellPrice;
    }

    public ExchangeRateEvent sellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        return this;
    }
}
