package cash.xcl.server.exch;


import static cash.xcl.api.exch.Side.BUY;
import static cash.xcl.api.exch.Side.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.Test;

import cash.xcl.api.exch.CurrencyPair;
import cash.xcl.api.exch.NewLimitOrderCommand;
import cash.xcl.api.exch.OrderClosedEvent.REASON;
import cash.xcl.api.exch.Side;
import cash.xcl.server.exch.ExchangeMarket.OrderClosedListener;
import cash.xcl.server.exch.ExchangeMarket.TradeListener;
import net.openhft.chronicle.core.Mocker;

public class ExchangeMarketTest {

    @Test
    public void userCancel() {
        CurrencyPair currencyPair = new CurrencyPair("EUR", "XCL");
        TradeListener tradeListener = Mocker.ignored(TradeListener.class);
        Queue<MethodCall> resultsQueue = new ArrayDeque<>();
        OrderClosedListener closedListener = Mocker.intercepting(OrderClosedListener.class,
                (name, params) -> resultsQueue.add(new MethodCall(name, params)), null);
        try (ExchangeMarket market = new ExchangeMarket(1, tradeListener, closedListener)) {
            market.setCurrentTime(101);
            market.executeOrder(new NewLimitOrderCommand(1, 100, BUY, 111, 200, currencyPair, 1000));
            assertEquals(1, market.getOrdersCount(Side.BUY));
            market.setCurrentTime(105);
            market.cancelOrder(1, 100);

            assertTrue(!resultsQueue.isEmpty());
            MethodCall call = resultsQueue.poll();
            Order order = call.getParams(0);
            assertEquals(1, order.getOwnerAddress());
            assertEquals(100, order.getOwnerOrderTime());
            assertEquals(REASON.USER_REQUEST, call.getParams(1));
            assertEquals(0, market.getOrdersCount(Side.BUY));

            market.setCurrentTime(205);
            market.executeOrder(new NewLimitOrderCommand(1, 200, SELL, 111, 200, currencyPair, 1000));
            assertEquals(1, market.getOrdersCount(Side.SELL));
            market.setCurrentTime(210);
            market.cancelOrder(1, 200);

            assertTrue(!resultsQueue.isEmpty());
            call = resultsQueue.poll();
            order = call.getParams(0);
            assertEquals(1, order.getOwnerAddress());
            assertEquals(200, order.getOwnerOrderTime());
            assertEquals(REASON.USER_REQUEST, call.getParams(1));
            assertEquals(0, market.getOrdersCount(Side.BUY));

        }
    }

    @Test
    public void timeoutCancel() {
        CurrencyPair currencyPair = new CurrencyPair("EUR", "XCL");
        TradeListener tradeListener = Mocker.ignored(TradeListener.class);
        Queue<MethodCall> resultsQueue = new ArrayDeque<>();
        OrderClosedListener closedListener = Mocker.intercepting(OrderClosedListener.class,
                (name, params) -> resultsQueue.add(new MethodCall(name, params)), null);
        try (ExchangeMarket market = new ExchangeMarket(1, tradeListener, closedListener)) {
            market.setCurrentTime(100);
            market.executeOrder(new NewLimitOrderCommand(1, 100, BUY, 111, 200.0, currencyPair, 1400));
            market.executeOrder(new NewLimitOrderCommand(1, 101, BUY, 111, 200.0, currencyPair, 1000));
            market.executeOrder(new NewLimitOrderCommand(1, 102, BUY, 111, 200.0, currencyPair, 10000));
            market.setCurrentTime(200);
            market.executeOrder(new NewLimitOrderCommand(1, 201, SELL, 111, 201.0, currencyPair, 1300));
            market.executeOrder(new NewLimitOrderCommand(1, 202, SELL, 111, 201.0, currencyPair, 1000));
            market.executeOrder(new NewLimitOrderCommand(1, 203, SELL, 111, 201.0, currencyPair, 10000));
            market.setCurrentTime(1500);
            assertEquals(3, market.getOrdersCount(Side.BUY));
            assertEquals(3, market.getOrdersCount(Side.SELL));
            market.removeExpired();
            assertEquals(4, resultsQueue.size());
            while (!resultsQueue.isEmpty()) {
                MethodCall call = resultsQueue.poll();
                Order order = call.getParams(0);
                assertEquals(1, order.getOwnerAddress());
                assertEquals(REASON.TIME_OUT, call.getParams(1));
            }
            assertEquals(1, market.getOrdersCount(Side.BUY));
            assertEquals(1, market.getOrdersCount(Side.SELL));
        }
    }

}
