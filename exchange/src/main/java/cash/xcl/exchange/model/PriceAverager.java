package cash.xcl.exchange.model;

import net.openhft.chronicle.core.Maths;

public class PriceAverager {
    private final double ewmaQuantity;
    private double ewmaPrice;
    private double initValue, initQuantity;

    public PriceAverager() {
        this(1e6);
    }

    public PriceAverager(double ewmaQuantity) {
        this.ewmaQuantity = ewmaQuantity;
    }

    public void sample(double price, double quantity) {
        if (quantity > 0) {
            if (initQuantity < ewmaQuantity) {
                initValue += price * quantity;
                initQuantity += quantity;
                ewmaPrice = initValue / initQuantity;

            } else if (quantity > ewmaQuantity) {
                ewmaPrice = price;

            } else {
                double alpha = quantity / ewmaQuantity;
                ewmaPrice = price * alpha + ewmaPrice * (1 - alpha);
            }
        }
    }

    public double ewmaPrice() {
        return Maths.round6(ewmaPrice);
    }
}
