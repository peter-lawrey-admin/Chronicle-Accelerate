package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ExchangeRateEvent extends SignedMessage {
    private String symbol1, symbol2;
    private double buyPrice, sellPrice;

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        symbol1 = bytes.readUtf8();
        symbol2 = bytes.readUtf8();
        buyPrice = bytes.readDouble();
        sellPrice = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeUtf8(symbol1);
        bytes.writeUtf8(symbol2);
        bytes.writeDouble(buyPrice);
        bytes.writeDouble(sellPrice);
    }

    @Override
    protected int messageType() {
        return MethodIds.EXCHANGE_RATE_EVENT;
    }
}
