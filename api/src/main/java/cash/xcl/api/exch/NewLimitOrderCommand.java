package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.api.dto.Validators.*;

public class NewLimitOrderCommand extends SignedMessage {

    private Side action;
    private long quantity;
    private double maxPrice;
    private CurrencyPair currencyPair;
    private long timeToLive;

    public NewLimitOrderCommand() {

    }

    public NewLimitOrderCommand(long sourceAddress, long eventTime, Side action, long qty, double maxPrice, CurrencyPair currencyPair,
                                long timeToLive) {
        super(sourceAddress, eventTime);
        this.action = action;
        setQuantity(qty);
        setMaxPrice(maxPrice);
        setCurrency(currencyPair);
        setTimeToLive(timeToLive);
    }


    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        this.action = Side.fromId(bytes.readInt());
        setQuantity(bytes.readLong());
        setMaxPrice(bytes.readDouble());
        if (currencyPair == null) {
            currencyPair = new CurrencyPair();
        }
        currencyPair.readMarshallable(bytes);
        setTimeToLive(bytes.readLong());
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeInt(action.ordinal());
        bytes.writeLong(quantity);
        bytes.writeDouble(maxPrice);
        currencyPair.writeMarshallable(bytes);
        bytes.writeLong(timeToLive);
    }

    @Override
    public int messageType() {
        return MessageTypes.NEW_ORDER_COMMAND;
    }


    public Side getAction() {
        return action;
    }

    public long getQuantity() {
        return quantity;
    }

    void setQuantity(long quantity) {
        this.quantity = strictPositive(quantity);
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    void setMaxPrice(double maxPrice) {
        this.maxPrice = notNaN(maxPrice); // it could be infinite if you really want to buy it
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    void setCurrency(CurrencyPair currencyPair) {
        this.currencyPair = notNull(currencyPair);
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    void setTimeToLive(long timeToLive) {
        this.timeToLive = positive(timeToLive);
    }
}
