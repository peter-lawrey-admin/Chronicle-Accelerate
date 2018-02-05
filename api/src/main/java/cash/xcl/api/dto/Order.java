package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

// FIXME needs reviewing/completing
public abstract class Order extends AbstractBytesMarshallable {

    private final String id;

    private final String accountAddress;

    private final Side side;

    private final double initialQuantity;

    private final String symbol1symbol2;

    private final long createdTime;

    private double filledQuantity;


    public Order(String id, String accountAddress, Side side, double initialQuantity, String symbol1symbol2, long createdTime, double filledQuantity) {
        this.id = id;
        this.accountAddress = accountAddress;
        this.side = side;
        this.initialQuantity = initialQuantity;
        this.symbol1symbol2 = symbol1symbol2;
        this.createdTime = createdTime;
        this.filledQuantity = filledQuantity;
    }

    public abstract boolean isLimitOrder();

    public abstract boolean isMarketOrder();


    public Side getSide() {
        return side;
    }



    boolean isFilled() {
        return false;
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {


    }

    @Override
    public void writeMarshallable(BytesOut bytes) {



    }


    public String id() {
        return id;
    }

    public String accountAddress() {
        return accountAddress;
    }

    public Side side() {
        return side;
    }

    public double initialQuantity() {
        return initialQuantity;
    }

    public String symbol1symbol2() {
        return symbol1symbol2;
    }

    public long createdTime() {
        return createdTime;
    }

    public double filledQuantity() {
        return filledQuantity;
    }

    public Order filledQuantity(double filledQuantity) {
        this.filledQuantity = filledQuantity;
        return this;
    }
}
