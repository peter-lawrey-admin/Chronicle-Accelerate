package cash.xcl.api.exch;


import static cash.xcl.api.exch.Side.getDefaultPrecision;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static java.lang.Double.MIN_VALUE;
import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import static java.lang.Math.nextUp;

import cash.xcl.api.exch.Side.PriceCompareResult;

public class SideTest {

    public static final double DELTA = nextUp(0.001);

    @Test(expected = IllegalArgumentException.class)
    public void testNaN1() {
        Side.BUY.compare(Double.NaN, 1234, DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNaN2() {
        Side.SELL.compare(1234, Double.NaN, DELTA);
    }

    @Test
    public void testCompare() {
        double[] newPrices = {9.9, 9.0, 10.0};
        double[] refPrices = {9.8, 9.1, 10.0};
        int[] expectedResult = {1, -1, 0};
        for (int i = 0; i < newPrices.length; i++) {
            for (Side side : Side.values()) {
                PriceCompareResult result = translate(expectedResult[i], side);
                assertTrue(side.compare(newPrices[i], refPrices[i], DELTA) == result);
                switch (result) {
                case BETTER:
                    assertTrue(side.isBetter(newPrices[i], refPrices[i], DELTA));
                    assertTrue(side.isBetterOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getBetter(newPrices[i], refPrices[i]), newPrices[i], DELTA);
                    assertEquals(side.getBetter(newPrices[i], refPrices[i]), side.other().getWorse(refPrices[i], newPrices[i]), DELTA);
                    assertFalse(side.isWorse(newPrices[i], refPrices[i], DELTA));
                    assertFalse(side.isWorseOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getWorse(newPrices[i], refPrices[i]), refPrices[i], DELTA);
                    assertFalse(side.isSame(newPrices[i], refPrices[i], DELTA));
                    assertTrue(abs(newPrices[i] - refPrices[i]) > DELTA);
                    break;
                case SAME:
                    assertFalse(side.isBetter(newPrices[i], refPrices[i], DELTA));
                    assertTrue(side.isBetterOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getBetter(newPrices[i], refPrices[i]), refPrices[i], DELTA);
                    assertFalse(side.isWorse(newPrices[i], refPrices[i], DELTA));
                    assertTrue(side.isWorseOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getWorse(newPrices[i], refPrices[i]), newPrices[i], DELTA);
                    assertTrue(side.isSame(newPrices[i], refPrices[i], DELTA));
                    assertTrue(abs(newPrices[i] - refPrices[i]) <= DELTA);
                    break;
                case WORSE:
                    assertFalse(side.isBetter(newPrices[i], refPrices[i], DELTA));
                    assertFalse(side.isBetterOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getBetter(newPrices[i], refPrices[i]), refPrices[i], DELTA);
                    assertEquals(side.getBetter(newPrices[i], refPrices[i]), side.other().getWorse(refPrices[i], newPrices[i]), DELTA);
                    assertTrue(side.isWorse(newPrices[i], refPrices[i], DELTA));
                    assertTrue(side.isWorseOrSame(newPrices[i], refPrices[i], DELTA));
                    assertEquals(side.getWorse(newPrices[i], refPrices[i]), newPrices[i], DELTA);
                    assertFalse(side.isSame(newPrices[i], refPrices[i], DELTA));
                    assertTrue(abs(newPrices[i] - refPrices[i]) > DELTA);
                    break;
                default:
                    throw new AssertionError();

                }
            }
        }
    }

    @Test
    public void testCompareVeryClosePrices() {
        double price = nextUp(10.0);
        Side.forEach((side) -> {
            assertEquals(PriceCompareResult.SAME, side.compare(price, side.improveBy(price, DELTA), DELTA));
            assertEquals(PriceCompareResult.SAME, side.compare(price, side.worsenBy(price, DELTA), DELTA));
            assertEquals(PriceCompareResult.WORSE, side.compare(price, side.improveBy(price, 2 * DELTA), DELTA));
            assertEquals(PriceCompareResult.BETTER, side.compare(price, side.worsenBy(price, 2 * DELTA), DELTA));
        });
    }

    @Test
    public void testCompareVerySmallPrices() {
        // double precision = DELTA;
        double price = 0;
        Side.forEach((side) -> {
            assertEquals(PriceCompareResult.SAME, side.compare(price, side.improveBy(price, MIN_VALUE), MIN_VALUE));
            assertEquals(PriceCompareResult.WORSE, side.compare(price, side.improveBy(price, 2 * MIN_VALUE), MIN_VALUE));
            assertEquals(PriceCompareResult.BETTER, side.compare(price, side.worsenBy(price, 2 * MIN_VALUE), MIN_VALUE));
        });
    }

    @Test
    public void testCompareBasic() {
        assertEquals(PriceCompareResult.WORSE, Side.BUY.compare(10.5, 11.0, DELTA));
        assertEquals(PriceCompareResult.BETTER, Side.BUY.compare(11.5, 11.0, DELTA));
        assertEquals(PriceCompareResult.SAME, Side.BUY.compare(11.5, 11.5 + MIN_VALUE, DELTA));
        assertEquals(PriceCompareResult.BETTER, Side.SELL.compare(10.5, 11.0, DELTA));
        assertEquals(PriceCompareResult.WORSE, Side.SELL.compare(11.5, 11.0, DELTA));
        assertEquals(PriceCompareResult.SAME, Side.SELL.compare(11.5, 11.5 + MIN_VALUE, DELTA));
    }

    private static PriceCompareResult translate(int price, Side side) {
        if (side == Side.BUY) {
            if (price > 0) {
                return PriceCompareResult.BETTER;
            } else if (price < 0) {
                return PriceCompareResult.WORSE;
            } else {
                return PriceCompareResult.SAME;
            }
        } else {
            if (price > 0) {
                return PriceCompareResult.WORSE;
            } else if (price < 0) {
                return PriceCompareResult.BETTER;
            } else {
                return PriceCompareResult.SAME;
            }
        }
    }

    @Test
    public void testGetBetterOrWorst() {
        double[] prices1 = {9.9, 9.0, 10.0};
        double[] prices2 = {9.8, 9.1, 10.0};
        assertEquals(Side.BUY.getWorse(prices1[0], prices2[0]), prices2[0], DELTA);
        assertEquals(Side.BUY.getBetter(prices1[0], prices2[0]), prices1[0], DELTA);

        assertEquals(Side.BUY.getWorse(prices1[1], prices2[1]), prices1[1], DELTA);
        assertEquals(Side.BUY.getBetter(prices1[1], prices2[1]), prices2[1], DELTA);

        assertEquals(Side.BUY.getWorse(prices1[2], prices2[2]), prices1[2], DELTA);
        assertEquals(Side.BUY.getBetter(prices1[2], prices2[2]), prices2[2], DELTA);

        assertEquals(Side.SELL.getWorse(prices1[0], prices2[0]), prices1[0], DELTA);
        assertEquals(Side.SELL.getBetter(prices1[0], prices2[0]), prices2[0], DELTA);

        assertEquals(Side.SELL.getWorse(prices1[1], prices2[1]), prices2[1], DELTA);
        assertEquals(Side.SELL.getBetter(prices1[1], prices2[1]), prices1[1], DELTA);

        assertEquals(Side.SELL.getWorse(prices1[2], prices2[2]), prices2[2], DELTA);
        assertEquals(Side.SELL.getBetter(prices1[2], prices2[2]), prices1[2], DELTA);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBetterOrWorstNaN1() {
        assertEquals(9, Side.BUY.getWorse(Double.NaN, 9), DELTA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBetterOrWorstNaN2() {
        assertEquals(9, Side.BUY.getBetter(9, Double.NaN), DELTA);
    }

    @Test
    public void testRoundBetter() {
        double value = 99.995;
        double tickSize = 0.01;
        double epsilon = tickSize / 10;
        Side.forEach((side) -> {
            double betterPrice = side.roundBetter(value, tickSize);
            assertTrue(side.isBetter(betterPrice, value, epsilon));
            assertTrue(side.isSame(betterPrice, side.roundBetter(value, tickSize), epsilon));
            assertTrue(isNaN(side.roundBetter(Double.NaN, tickSize)));
            assertTrue(isNaN(side.roundBetter(value, Double.NaN)));
        });
    }

    @Test
    public void testRoundWorse() {
        double value = 99.995;
        double tickSize = 0.01;
        double epsilon = tickSize / 10;
        Side.forEach((side) -> {
            double worsePrice = side.roundWorse(value, tickSize);
            assertTrue(side.isWorse(worsePrice, value, epsilon));
            assertTrue(side.isSame(worsePrice, side.roundWorse(value, tickSize), epsilon));
            assertTrue(isNaN(side.roundWorse(Double.NaN, tickSize)));
            assertTrue(isNaN(side.roundWorse(value, Double.NaN)));
        });
        assertEquals(10, Side.BUY.roundWorse(13.5, 5), epsilon);
        assertEquals(15, Side.SELL.roundWorse(13.5, 5), epsilon);
    }

    @Test
    public void testImproveBy() {
        double value = 99.995;
        double tickSize = 0.01;
        double epsilon = tickSize / 10;
        Side.forEach((side) -> {
            double betterPrice = side.improveBy(value, tickSize);
            assertTrue(side.isBetter(betterPrice, value, epsilon));
            assertTrue(isNaN(side.improveBy(Double.NaN, tickSize)));
            assertTrue(isNaN(side.improveBy(value, Double.NaN)));
        });
    }

    @Test
    public void testWorsenBy() {
        double value = 99.995;
        double tickSize = 0.01;
        double epsilon = tickSize / 10;
        Side.forEach((side) -> {
            double worsenPrice = side.worsenBy(value, tickSize);
            assertTrue(side.isWorse(worsenPrice, value, epsilon));
            assertTrue(isNaN(side.worsenBy(Double.NaN, tickSize)));
            assertTrue(isNaN(side.worsenBy(value, Double.NaN)));
        });
    }

    @Test
    public void testRoundBetterVsRoundWorse() {
        double value = 99.995;
        double tickSize = 0.01;
        double epsilon = tickSize / 10;
        Side.forEach((side) -> {
            double betterPrice = side.roundBetter(value, tickSize);
            double worsPrice = side.roundWorse(value, tickSize);
            assertTrue(side.isBetter(betterPrice, worsPrice, epsilon));
        });
    }

    @Test
    public void testAccept() {
        final AtomicInteger counter = new AtomicInteger(0);
        Side.forEach((s) -> counter.incrementAndGet());
        assertTrue(counter.get() == 2);
    }

    @Test
    public void testDefaultPrecision() {
        int tickSize = 100;
        assertTrue(getDefaultPrecision(tickSize) < tickSize);
        assertTrue(getDefaultPrecision(tickSize) > 0);
        assertTrue((tickSize + getDefaultPrecision(tickSize)) < (tickSize * 2));
    }

    @Test
    public void testTicksBetween() {
        assertTrue(Side.ticksBetween(90, 100, 10) == 1);
        assertTrue(Side.ticksBetween(90, 100, 5) == 2);
        assertTrue(Side.ticksBetween(100, 90, 1) == 10);
        assertTrue(Side.ticksBetween(100, 100, 1) == 0);
        assertTrue(Side.ticksBetween(100, 100, 100) == 0);
        assertTrue(Side.ticksBetween(100, 100, 0.05) == 0);
    }

}
