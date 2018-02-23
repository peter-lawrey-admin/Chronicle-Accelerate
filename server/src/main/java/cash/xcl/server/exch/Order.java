package cash.xcl.server.exch;

import java.util.Comparator;

import cash.xcl.api.exch.Side;
import net.openhft.chronicle.wire.AbstractMarshallable;

class Order extends AbstractMarshallable {

    private final long ownerAddress;
    private final long ownerOrderTime;
    private final long orderId;
    private final long expires;
    private final long quantity;
    private final double price;
    private final Side side;
    private long filled = 0;

    Order(long orderId, Side side, long quantity, double price, long expires, long ownerAddress, long ownerOrderTime) {
        this.orderId = orderId;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.expires = expires;
        this.ownerAddress = ownerAddress;
        this.ownerOrderTime = ownerOrderTime;
    }


    long getQuantityLeft() {
        return quantity - filled;
    }

    long getQuantity() {
        return quantity;
    }

    long fill(long fillQty) {
        assert fillQty <= getQuantityLeft();
        filled += fillQty;
        return getQuantityLeft();
    }

    long getExpirationTime() {
        return expires;
    }

    double getPrice() {
        return price;
    }

    long getOrderId() {
        return orderId;
    }

    long getOwnerAddress() {
        return ownerAddress;
    }

    long getOwnerOrderTime() {
        return ownerOrderTime;
    }

    boolean matches(long address, long orderTime) {
        return (ownerAddress == address) && (ownerOrderTime == orderTime);
    }

    Side getSide() {
        return side;
    }

    static Comparator<Order> getBuyComparator() {
        return new PriceComparator().reversed().thenComparing((o1, o2) -> Double.compare(o1.orderId, o2.orderId));
    }

    static Comparator<Order> getSellComparator() {
        return new PriceComparator().thenComparing((o1, o2) -> Double.compare(o1.orderId, o2.orderId));
    }

    private static class PriceComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            assert (o1 != null) && (o2 != null);
            return Double.compare(o1.price, o2.price);
        }
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }

    @Override
    public boolean equals(Object obj) {
        assert (obj != null) && (obj instanceof Order);
        return (this == obj) || (orderId == ((Order) obj).orderId);
    }


}
