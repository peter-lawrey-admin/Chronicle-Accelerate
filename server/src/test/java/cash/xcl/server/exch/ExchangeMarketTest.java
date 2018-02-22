package cash.xcl.server.exch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import cash.xcl.api.exch.CurrencyPair;
import cash.xcl.api.exch.NewLimitOrderCommand;

public class ExchangeMarketTest {

    @Test
    public void userCancel() {
        try (ExchangeMarket market = new ExchangeMarket(2.5)) {
            market.setCurrentTime(101);
            CurrencyPair currencyPair = new CurrencyPair("EUR", "XCL");
            market.processOrder(new NewLimitOrderCommand(1, 100, true, 111, 200, currencyPair, 1000));
            assertEquals(1, market.getOrdersCount(Side.BUY));
            market.setCurrentTime(105);
            AtomicBoolean cancelBuyCalled = new AtomicBoolean();
            market.cancelOrder(1, 100, (order) -> {
                assertEquals(1, order.getOwnerAddress());
                assertEquals(100, order.getOwnerOrderTime());
                cancelBuyCalled.set(true);
            });
            assertTrue(cancelBuyCalled.get());
            assertEquals(0, market.getOrdersCount(Side.BUY));
            market.setCurrentTime(205);
            market.processOrder(new NewLimitOrderCommand(1, 200, false, 111, 200, currencyPair, 1000));
            assertEquals(1, market.getOrdersCount(Side.SELL));
            market.setCurrentTime(210);
            AtomicBoolean cancelSellCalled = new AtomicBoolean();
            market.cancelOrder(1, 200, (order) -> {
                assertEquals(1, order.getOwnerAddress());
                assertEquals(200, order.getOwnerOrderTime());
                cancelSellCalled.set(true);
            });
            assertTrue(cancelSellCalled.get());
            assertEquals(0, market.getOrdersCount(Side.SELL));
        }
    }

    @Test
    public void timeoutCancel() {
        CurrencyPair currencyPair = new CurrencyPair("EUR", "XCL");
        try (ExchangeMarket market = new ExchangeMarket(2.5)) {
            market.setCurrentTime(100);
            market.processOrder(new NewLimitOrderCommand(1, 100, true, 111, 200.0, currencyPair, 1400));
            market.processOrder(new NewLimitOrderCommand(1, 101, true, 111, 200.0, currencyPair, 1000));
            market.processOrder(new NewLimitOrderCommand(1, 102, true, 111, 200.0, currencyPair, 10000));
            market.setCurrentTime(200);
            market.processOrder(new NewLimitOrderCommand(1, 201, false, 111, 201.0, currencyPair, 1300));
            market.processOrder(new NewLimitOrderCommand(1, 202, false, 111, 201.0, currencyPair, 1000));
            market.processOrder(new NewLimitOrderCommand(1, 203, false, 111, 201.0, currencyPair, 10000));
            market.setCurrentTime(1500);
            AtomicInteger count = new AtomicInteger();
            market.removeExpired((order) -> {
                assertTrue(order.getExpirationTime() <= 1500);
                count.incrementAndGet();
            });
            assertEquals(4, count.get());
        }
    }

}
