package cash.xcl.api.dto;

// FIXME needs reviewing/completing
public class MarketOrder extends Order {

    public MarketOrder(String id, String accountAddress, Side side, double initialQuantity, String symbol1symbol2, long createdTime, double filledQuantity) {
        super(id, accountAddress, side, initialQuantity, symbol1symbol2, createdTime, filledQuantity);
    }

    @Override
    public boolean isLimitOrder() {
        return false;
    }

    @Override
    public boolean isMarketOrder() {
        return true;
    }

}
