package cash.xcl.exchange.model;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CancelOrderCommand;
import cash.xcl.api.dto.ExecutionReportEvent;
import cash.xcl.api.dto.NewOrderCommand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderBook {
    private final boolean isBuy; // is ascending
    private final List<Order> limitOrders = new ArrayList<>();
    private final List<Order> marketOrders = new ArrayList<>();
    private final Comparator<Order> comparator;
    private int next = 0;

    public OrderBook(boolean isBuy) {
        this.isBuy = isBuy;
        comparator = isBuy ? OrderComparator.BUY : OrderComparator.SELL;
    }

    /**
     * @throws ArrayIndexOutOfBoundsException if called incorrectly.
     */
    public Order nextOrder() throws ArrayIndexOutOfBoundsException {
        return next >= limitOrders.size()
                ? marketOrders.get(next - limitOrders.size())
                : limitOrders.get(next);
    }

    public void advanceNext() {
        next--;
    }

    public boolean hasMoreOrders() {
        return next >= 0;
    }

    public void matched(AllMessages lookup, double price, double quantityMatched) {
        resetNext();
        double quantityRemaining;
        Order order;
        while (true) {
            if (!hasMoreOrders())
                return;
            order = nextOrder();
            quantityRemaining = order.quantity() - order.filledQuantity();
            if (quantityMatched < quantityRemaining)
                break;
            quantityMatched -= quantityRemaining;
            order.filledQuantity(order.quantity());
            ExecutionReportEvent ere = new ExecutionReportEvent(0, 0, order.sourceAddress(), order.clientOrderId(), price, quantityRemaining, 0.0);
            lookup.executionReportEvent(ere);
            removeOrder();
            advanceNext();
        }

        order.filledQuantity(order.filledQuantity() + quantityMatched);
        quantityRemaining -= quantityMatched;
        ExecutionReportEvent ere = new ExecutionReportEvent(0, 0, order.sourceAddress(), order.clientOrderId(), price, quantityMatched, quantityRemaining);
        lookup.executionReportEvent(ere);
        removeOrder();
    }

    public void cancelAllRestlessOrders(AllMessages lookup) {
        resetNext();
        while (hasMoreOrders()) {
            Order order = nextOrder();
            if (!order.isResting()) {
                sendCancelledER(lookup, order);
            }
            advanceNext();
        }
    }

    public boolean cancelOrder(AllMessages lookup, CancelOrderCommand coc) {
        resetNext();
        while (hasMoreOrders()) {
            Order order = nextOrder();
            if (order.clientOrderId().equals(coc.clientOrderId()) && order.sourceAddress() == coc.sourceAddress()) {
                sendCancelledER(lookup, order);
                return true;
            }
            advanceNext();
        }
        return false;
    }

    private void sendCancelledER(AllMessages lookup, Order order) {
        removeOrder();
        ExecutionReportEvent ere = new ExecutionReportEvent(0, 0, order.sourceAddress(), order.clientOrderId(), Double.NaN, 0.0, 0.0);
        lookup.executionReportEvent(ere);
    }

    private void removeOrder() {
        if (next >= limitOrders.size())
            marketOrders.remove(next - limitOrders.size());
        else
            limitOrders.remove(next);
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void addOrder(long startOfRoundTime, NewOrderCommand newOrderCommand) {
        // todo add recycling.
        Order order = new Order(startOfRoundTime, newOrderCommand);
        if (newOrderCommand.isMarket())
            marketOrders.add(order);
        else
            limitOrders.add(order);
    }

    public void startEOR() {
        resetNext();
        limitOrders.sort(comparator);
    }

    private void resetNext() {
        next = limitOrders.size() + marketOrders.size() - 1;
    }

    enum OrderComparator implements Comparator<Order> {
        BUY {
            @Override
            public int compare(Order o1, Order o2) {
                int cmp = Double.compare(o1.price(), o2.price());
                if (cmp != 0) return cmp;
                return -Long.compare(o1.timeAdded(), o2.timeAdded()); // oldest at the top.
            }
        },
        SELL {
            @Override
            public int compare(Order o1, Order o2) {
                int cmp = -Double.compare(o1.price(), o2.price());
                if (cmp != 0) return cmp;
                return -Long.compare(o1.timeAdded(), o2.timeAdded()); // oldest at the top.
            }
        }
    }
}
