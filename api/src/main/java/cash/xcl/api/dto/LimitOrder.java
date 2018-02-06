package cash.xcl.api.dto;

// FIXME needs reviewing/completing
public class LimitOrder extends Order {

    private double limitPrice;

    public LimitOrder(String id,
                      String address,
                      Side side,
                      double initialQuantity,
                      String symbol1symbol2,
                      long createdTime,
                      double filledQuantity,
                      double limitPrice) {
        super(id, address, side, initialQuantity, symbol1symbol2, createdTime, filledQuantity);
        this.limitPrice = limitPrice;
    }

    @Override
    public boolean isLimitOrder() {
        return true;
    }

    @Override
    public boolean isMarketOrder() {
        return false;
    }

    public double limitPrice() {
        return limitPrice;
    }

    public LimitOrder limitPrice(double limitPrice) {
        this.limitPrice = limitPrice;
        return this;
    }
}
