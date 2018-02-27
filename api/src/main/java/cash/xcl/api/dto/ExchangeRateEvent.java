package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class ExchangeRateEvent extends SignedMessage {
    private String symbol1symbol2;
    private double midPrice, spreadPrice;

    public ExchangeRateEvent(long sourceAddress, long eventTime, String symbol1symbol2, double midPrice, double spreadPrice) {
        super(sourceAddress, eventTime);
        this.symbol1symbol2 = symbol1symbol2;
        this.midPrice = midPrice;
        this.spreadPrice = spreadPrice;
    }

    public ExchangeRateEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        symbol1symbol2 = bytes.readUtf8();
        midPrice = bytes.readDouble();
        spreadPrice = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUtf8(symbol1symbol2);
        bytes.writeDouble(midPrice);
        bytes.writeDouble(spreadPrice);
    }

    @Override
    public int messageType() {
        return MessageTypes.EXCHANGE_RATE_EVENT;
    }

    public String symbol1symbol2() {
        return symbol1symbol2;
    }

    public ExchangeRateEvent symbol1symbol2(String symbol1symbol2) {
        this.symbol1symbol2 = symbol1symbol2;
        return this;
    }

    public double midPrice() {
        return midPrice;
    }

    public ExchangeRateEvent midPrice(double midPrice) {
        this.midPrice = midPrice;
        return this;
    }

    public double spreadPrice() {
        return spreadPrice;
    }

    public ExchangeRateEvent spreadPrice(double spreadPrice) {
        this.spreadPrice = spreadPrice;
        return this;
    }
}
