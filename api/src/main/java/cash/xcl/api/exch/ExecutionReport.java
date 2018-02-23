package cash.xcl.api.exch;

import static cash.xcl.api.dto.Validators.notNull;
import static cash.xcl.api.dto.Validators.strictPositive;
import static cash.xcl.api.dto.Validators.validNumber;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class ExecutionReport extends AbstractBytesMarshallable {

    private CurrencyPair pair;
    private Side action;
    private long quantity;
    private double price;
    private long initiator;
    private long aggressor;

    public ExecutionReport() {
    }

    public ExecutionReport(CurrencyPair pair, Side side, long quantity, double price, long aggressor, long initiator) {
        this.pair = pair;
        this.action = side;
        this.quantity = quantity;
        this.price = price;
        this.initiator = initiator;
        this.aggressor = aggressor;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {
        if (pair == null) {
            pair = new CurrencyPair();
        }
        pair.readMarshallable(bytes);
        setAction((Side) bytes.<Side>readEnum(Side.class));
        setQuantity(bytes.readLong());
        setPrice(price);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void writeMarshallable(BytesOut bytes) {
        pair.writeMarshallable(bytes);
        bytes.<Side>writeEnum(action);
        bytes.writeLong(quantity);
        bytes.writeDouble(price);
    }

    public CurrencyPair getPair() {
        return pair;
    }

    void setPair(CurrencyPair pair) {
        this.pair = notNull(pair);
    }

    public Side getAction() {
        return action;
    }

    void setAction(Side action) {
        this.action = notNull(action);
    }

    public long getQuantity() {
        return quantity;
    }

    void setQuantity(long quantity) {
        this.quantity = strictPositive(quantity);
    }

    public double getPrice() {
        return price;
    }

    void setPrice(double price) {
        this.price = validNumber(price);
    }

    public long getInitiator() {
        return initiator;
    }

    void setInitiator(long initiator) {
        this.initiator = initiator;
    }

    public long getAggressor() {
        return aggressor;
    }

    void setAggressor(long partner) {
        this.aggressor = partner;
    }


}
