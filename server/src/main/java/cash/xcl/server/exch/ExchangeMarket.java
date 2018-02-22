package cash.xcl.server.exch;

import static cash.xcl.api.dto.Validators.positive;
import static cash.xcl.api.dto.Validators.validNumber;

import java.io.Closeable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.function.Consumer;

import cash.xcl.api.exch.NewLimitOrderCommand;
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

    private long currentTime = 0;
    private long idGen = 1;

    ExchangeMarket(double tickSize) {
        this.tickSize = positive(validNumber(tickSize));
        this.precision = Side.getDefaultPrecision(tickSize);
    }


    private TreeSet<Order> getMarket(boolean buySide) {
        if (buySide) {
            return buyOrders;
        } else {
            return sellOrders;
        }
    }

    private TreeSet<Order> getMarket(Side side) {
        if (side == Side.BUY) {
            return buyOrders;
        } else {
            return sellOrders;
        }
    }

    private Side getSide(boolean buySide) {
        if (buySide) {
            return Side.BUY;
        } else {
            return Side.SELL;
        }
    }


    void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    void processOrder(NewLimitOrderCommand newLimitOrderCommand) {
        long orderId = idGen++;
        Side orderSide = getSide(newLimitOrderCommand.isBuyAction());
        double orderPrice = orderSide.roundWorse(newLimitOrderCommand.getMaxPrice(), tickSize);
        Order newOrder = new Order(orderId, newLimitOrderCommand.getQuantity(), orderPrice,
                newLimitOrderCommand.getTimeToLive() + currentTime, newLimitOrderCommand.sourceAddress(), newLimitOrderCommand.eventTime());
        TreeSet<Order> sideToMatch = getMarket(!newLimitOrderCommand.isBuyAction());
        Iterator<Order> it = sideToMatch.iterator();
        while (it.hasNext()) {
            Order topOrder = it.next();
            if (topOrder.getExpirationTime() <= currentTime) {
                it.remove();
                expirationOrder.remove(topOrder);
                // send order expired event
            } else {
                if (orderSide.isBetterOrSame(orderPrice, topOrder.getPrice(), precision)) {
                    if (newOrder.getQuantityLeft() <= topOrder.getQuantityLeft()) { // complete fill
                        long fillQty = newOrder.getQuantityLeft();
                        newOrder.fill(fillQty);
                        topOrder.fill(fillQty);
                        if (topOrder.getQuantityLeft() == 0) {
                            it.remove();
                            expirationOrder.remove(topOrder);
                        }
                        // send the message
                        break;
                    } else {
                        // partial fill
                        long fillQty = topOrder.getQuantityLeft();
                        newOrder.fill(fillQty);
                        topOrder.fill(fillQty);
                        // send the message
                        it.remove();
                        expirationOrder.remove(topOrder);
                    }
                } else {
                    break;
                }
            }
        }
        if ((newLimitOrderCommand.getTimeToLive() > 0) && (newOrder.getQuantityLeft() > 0)) {
            getMarket(newLimitOrderCommand.isBuyAction()).add(newOrder);
            expirationOrder.add(newOrder);
        }
    }

    /**
     * inefficient, but good for testing
     */
    void removeExpired(Consumer<Order> processor) {
        assert expirationOrder.size() == (buyOrders.size() + sellOrders.size());
        while (!expirationOrder.isEmpty()) {
            Order order = expirationOrder.peek();
            if (order.getExpirationTime() <= currentTime) {
                expirationOrder.poll();
                // we don't know if is a buy or sell order, but we could figure out later
                // either by having separate priority queues or, buy assigning odd order ids to buy and even to sell
                if (!buyOrders.remove(order)) {
                    sellOrders.remove(order);
                }
                processor.accept(order);
            } else {
                break;
            }
        }
        assert expirationOrder.size() == (buyOrders.size() + sellOrders.size());
    }

    void cancelOrder(long sourceAddress, long orderTime, Consumer<Order> processor) {
        processor.accept(Side.<Order>applyOnce((side) -> findOrder(side, sourceAddress, orderTime)));
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


}
