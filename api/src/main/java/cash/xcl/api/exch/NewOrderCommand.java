package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedBinaryMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.util.Validators.*;

public class NewOrderCommand extends SignedBinaryMessage {

    private Side action;
    private double quantity;
    private double maxPrice;
    private CurrencyPair currencyPair;
    private long timeToLive; // in milliseconds

    public NewOrderCommand() {

    }

    public NewOrderCommand(long sourceAddress, long eventTime, Side action, double qty, double maxPrice, CurrencyPair currencyPair,
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
        quantity = 0;
        if (bytes.readRemaining() > 0)
            setQuantity(bytes.readDouble());
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
        bytes.writeDouble(quantity);
        bytes.writeDouble(maxPrice);
        currencyPair.writeMarshallable(bytes);
        bytes.writeLong(timeToLive);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.NEW_ORDER_COMMAND;
    }


    public Side getAction() {
        return action;
    }

    public double getQuantity() {
        return quantity;
    }

    void setQuantity(double quantity) {
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
