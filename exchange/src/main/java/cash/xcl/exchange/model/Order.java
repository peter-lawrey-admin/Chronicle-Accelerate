package cash.xcl.exchange.model;

import cash.xcl.api.dto.NewOrderCommand;
import net.openhft.chronicle.wire.AbstractMarshallable;

// FIXME needs reviewing/completing
public class Order extends AbstractMarshallable {

    private long orderId;

    private long sourceAddress;

    private long eventTime;

    private String clientOrderId;

    private boolean isBuy;

    private boolean isResting;

    private double quantity;

    private double price;

    private double filledQuantity;

    public Order(long orderId, NewOrderCommand noc) {
        init(orderId, noc);
    }

    public void init(long orderId, NewOrderCommand noc) {
        this.orderId = orderId;
        sourceAddress = noc.sourceAddress();
        eventTime = noc.eventTime();
        clientOrderId = noc.clientOrderId();
        isBuy = noc.isBuy();
        isResting = noc.isResting();
        quantity = noc.quantity();
        price = noc.price();
        filledQuantity = 0.0;
    }

    public long timeAdded() {
        return orderId;
    }

    public Order timeAdded(long timeAdded) {
        this.orderId = timeAdded;
        return this;
    }

    public long sourceAddress() {
        return sourceAddress;
    }

    public Order sourceAddress(long sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }

    public long eventTime() {
        return eventTime;
    }

    public Order eventTime(long eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    public String clientOrderId() {
        return clientOrderId;
    }

    public Order clientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
        return this;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public Order isBuy(boolean buy) {
        this.isBuy = buy;
        return this;
    }

    public boolean isResting() {
        return isResting;
    }

    public Order isResting(boolean resting) {
        this.isResting = resting;
        return this;
    }

    public double quantity() {
        return quantity;
    }

    public Order quantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public double price() {
        return price;
    }

    public Order price(double price) {
        this.price = price;
        return this;
    }

    public double filledQuantity() {
        return filledQuantity;
    }

    public Order filledQuantity(double filledQuantity) {
        this.filledQuantity = filledQuantity;
        return this;
    }

    public boolean isMarket() {
        return Double.isNaN(price);
    }
}
