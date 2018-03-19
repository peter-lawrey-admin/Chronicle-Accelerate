package cash.xcl.server.exch;

import org.junit.Test;

import static cash.xcl.api.exch.Side.BUY;
import static cash.xcl.api.exch.Side.SELL;
import static org.junit.Assert.*;

public class OrderTest {

    @Test
    public void basicTest() {
        Order order1 = new Order(1, BUY, 100, 1.25, 1000, 1, 900);
        Order order2 = new Order(2, SELL, 100, 1.25, 1000, 1, 901);
        assertEquals(1, order1.getOrderId());
        assertTrue(order1.matches(1, 900));
        assertFalse(order1.matches(1, 902));
        assertFalse(order1.matches(2, 903));
        assertEquals(2, order2.getOrderId());
        assertEquals(order1, order1);
        assertNotEquals(order1, order2);
        assertEquals(order2, order2);
        assertNotEquals(order1.hashCode(), order2.hashCode());
        assertEquals(70, order1.fill(30), 0);
        assertEquals(20, order1.fill(50), 0);
        assertEquals(0, order1.fill(20), 0);
        assertEquals(0, order2.fill(100), 0);
    }

}
