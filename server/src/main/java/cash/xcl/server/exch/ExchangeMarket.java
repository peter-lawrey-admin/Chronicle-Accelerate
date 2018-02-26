package cash.xcl.server.exch;

import static cash.xcl.api.dto.Validators.positive;
import static cash.xcl.api.dto.Validators.validNumber;
import static cash.xcl.api.exch.Side.BUY;
import static cash.xcl.api.exch.Side.SELL;

import java.io.Closeable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeSet;

import static java.lang.Math.min;

import cash.xcl.api.exch.NewLimitOrderCommand;
import cash.xcl.api.exch.OrderClosedEvent.REASON;
import cash.xcl.api.exch.Side;
import net.openhft.chronicle.core.annotation.SingleThreaded;

@SingleThreaded
class ExchangeMarket implements Closeable {
    // This is just POC implementation. The interface is ok, however
    // in order to be scalable the internal structure of the class must be changed,
    // this data structures will have to be changed on something much faster
    // probably an linked list for the orders, plus an index as map or tree, also I could think of bloom filter
    // for orders to find them quickly when we cancel them
    private final TreeSet<Order> buyOrders = new TreeSet<>(Order.getBuyComparator());
    private final TreeSet<Order> sellOrders = new TreeSet<>(Order.getSellComparator());
    private final PriorityQueue<Order> expirationOrder = new PriorityQueue<>(
            (o1, o2) -> Long.compare(o1.getExpirationTime(), o2.getExpirationTime()));

    private final double tickSize;
    private final double precision;
    private final OrderClosedListener closedListener;
    private final TradeListener tradeListener;

    private long currentReferenceTimeinMillis = 0;
    private long idGen = 1;

    ExchangeMarket(double tickSize, TradeListener tradeListener, OrderClosedListener closedListener) {
        this.tradeListener = tradeListener;
        this.closedListener = closedListener;
        this.tickSize = positive(validNumber(tickSize));
        this.precision = Side.getDefaultPrecision(tickSize);
    }


    private TreeSet<Order> getMarket(Side side) {
        if (side == Side.BUY) {
            return buyOrders;
        } else {
            return sellOrders;
        }
    }

    void setCurrentTime(long currentTime) {
        this.currentReferenceTimeinMillis = currentTime;
    }

    void executeOrder(NewLimitOrderCommand newLimitOrderCommand) {
        long orderId = idGen++;
        Side orderSide = newLimitOrderCommand.getAction();
        double orderPrice = orderSide.roundWorse(newLimitOrderCommand.getMaxPrice(), tickSize);
        Order newOrder = new Order(orderId, orderSide, newLimitOrderCommand.getQuantity(), orderPrice,
                newLimitOrderCommand.getTimeToLive() + currentReferenceTimeinMillis, newLimitOrderCommand.sourceAddress(), newLimitOrderCommand.eventTime());
        TreeSet<Order> sideToMatch = getMarket(orderSide.other());
        Iterator<Order> it = sideToMatch.iterator();
        while (it.hasNext()) {
            Order topOrder = it.next();
            if (topOrder.getExpirationTime() <= currentReferenceTimeinMillis) {
                it.remove();
                expirationOrder.remove(topOrder);
                closedListener.onClosed(topOrder, REASON.TIME_OUT);
                // send order expired event
            } else {
                if (orderSide.isBetterOrSame(orderPrice, topOrder.getPrice(), precision)) {
                    long fillQty = min(newOrder.getQuantityLeft(), topOrder.getQuantityLeft());
                    newOrder.fill(fillQty);
                    topOrder.fill(fillQty);
                    tradeListener.onTrade(newOrder, topOrder, fillQty);
                    if (topOrder.getQuantityLeft() == 0) {
                        it.remove();
                        expirationOrder.remove(topOrder);
                    }
                    if (newOrder.getQuantityLeft() == 0) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        if ((newOrder.getQuantityLeft() > 0)) {
            assert newOrder.getExpirationTime() >= currentReferenceTimeinMillis;
            if ((newOrder.getExpirationTime() > currentReferenceTimeinMillis)) {
                getMarket(orderSide).add(newOrder);
                expirationOrder.add(newOrder);
            }
        }
    }

    /**
     * inefficient, but good for testing
     */
    void removeExpired() {
        assert expirationOrder.size() == (buyOrders.size() + sellOrders.size());
        while (!expirationOrder.isEmpty()) {
            Order order = expirationOrder.peek();
            if (order.getExpirationTime() <= currentReferenceTimeinMillis) {
                expirationOrder.poll();
                // we don't know if is a buy or sell order, but we could figure out later
                // either by having separate priority queues or, buy assigning odd order ids to buy and even to sell
                if (!buyOrders.remove(order)) {
                    sellOrders.remove(order);
                }
                closedListener.onClosed(order, REASON.TIME_OUT);
            } else {
                break;
            }
        }
        assert expirationOrder.size() == (buyOrders.size() + sellOrders.size());
    }

    void cancelOrder(long sourceAddress, long orderTime) {
        Order toCancel = findOrder(BUY, sourceAddress, orderTime);
        if (toCancel == null) {
            toCancel = findOrder(SELL, sourceAddress, orderTime);
        }
        if (toCancel != null) {
            closedListener.onClosed(toCancel, REASON.USER_REQUEST);
        }
    }


    /**
     * VERY VERY inefficient, but good for testing
     */
    private Order findOrder(Side side, long sourceAddress, long orderTime) {
        TreeSet<Order> market = getMarket(side);
        Iterator<Order> it = market.iterator();
        while (it.hasNext()) {
            Order order = it.next();
            if (order.matches(sourceAddress, orderTime)) {
                it.remove();
                expirationOrder.remove(order);
                return order;
            }
        }
        return null;
    }

    int getOrdersCount(Side side) {
        return getMarket(side).size();
    }


    @Override
    public void close() {
        buyOrders.clear();
        sellOrders.clear();
        expirationOrder.clear();
    }

    @FunctionalInterface
    public static interface OrderClosedListener {
        void onClosed(Order order, REASON reason);
    }

    @FunctionalInterface
    public static interface TradeListener {
        void onTrade(Order aggressive, Order initiator, long qty);
    }

}
