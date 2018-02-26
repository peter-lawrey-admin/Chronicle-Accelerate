package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class NewOrderCommand extends SignedMessage {

    private String clientOrderId;

    private String symbol1symbol2;

    private boolean isBuy;

    private boolean isResting;

    private double quantity;

    private double price;

    public NewOrderCommand(long sourceAddress,
                           long eventTime,
                           String clientOrderId,
                           String symbol1symbol2,
                           boolean isBuy,
                           boolean isResting,
                           double quantity,
                           double price) {

        super(sourceAddress, eventTime);
        this.clientOrderId = clientOrderId;
        this.symbol1symbol2 = symbol1symbol2;
        this.isBuy = isBuy;
        this.isResting = isResting;
        this.quantity = quantity;
        this.price = price;
    }

    public NewOrderCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        clientOrderId = bytes.readUtf8();
        symbol1symbol2 = bytes.readUtf8();
        isBuy = bytes.readBoolean();
        isResting = bytes.readBoolean();
        quantity = bytes.readDouble();
        price = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUtf8(clientOrderId);
        bytes.writeUtf8(symbol1symbol2);
        bytes.writeBoolean(isBuy);
        bytes.writeBoolean(isResting);
        bytes.writeDouble(quantity);
        bytes.writeDouble(price);
    }

    public boolean isMarket() {
        return Double.isNaN(price);
    }

    public String clientOrderId() {
        return clientOrderId;
    }

    public NewOrderCommand clientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
        return this;
    }

    public String symbol1symbol2() {
        return symbol1symbol2;
    }

    public NewOrderCommand symbol1symbol2(String symbol1symbol2) {
        this.symbol1symbol2 = symbol1symbol2;
        return this;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public NewOrderCommand isBuy(boolean buy) {
        this.isBuy = buy;
        return this;
    }

    public boolean isResting() {
        return isResting;
    }

    public NewOrderCommand isResting(boolean resting) {
        this.isResting = resting;
        return this;
    }

    public double quantity() {
        return quantity;
    }

    public NewOrderCommand quantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public double price() {
        return price;
    }

    public NewOrderCommand price(double price) {
        this.price = price;
        return this;
    }

    @Override
    public int messageType() {
        return MessageTypes.NEW_ORDER_COMMAND;
    }
}
