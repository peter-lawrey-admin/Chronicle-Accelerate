package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
public class ExchangeRateResponse extends SignedBinaryMessage {

    private String symbol1symbol2;
    private double midPrice;
    private double spreadPrice;

    public ExchangeRateResponse(long sourceAddress,
                                long eventTime,
                                String symbol1symbol2,
                                double midPrice,
                                double spreadPrice) {
        super(sourceAddress, eventTime);
        this.symbol1symbol2 = symbol1symbol2;
        this.midPrice = midPrice;
        this.spreadPrice = spreadPrice;
    }

    public ExchangeRateResponse init(   long sourceAddress,
                                        long eventTime,
                                        String symbol1symbol2,
                                        double midPrice,
                                        double spreadPrice) {
        super.init(sourceAddress, eventTime);
        this.symbol1symbol2 = symbol1symbol2;
        this.midPrice = midPrice;
        this.spreadPrice = spreadPrice;
        return this;
    }


    public ExchangeRateResponse() {
        super();
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
    public int intMessageType() {

        return MessageTypes.EXCHANGE_RATE_RESPONSE;
    }
}
