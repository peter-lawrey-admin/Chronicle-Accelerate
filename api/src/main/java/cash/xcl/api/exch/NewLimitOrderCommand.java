package cash.xcl.api.exch;

import static cash.xcl.api.dto.Validators.notNaN;
import static cash.xcl.api.dto.Validators.notNullOrEmpty;
import static cash.xcl.api.dto.Validators.positive;
import static cash.xcl.api.dto.Validators.strictPositive;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class NewLimitOrderCommand extends SignedMessage {

    private boolean buyAction;
    private long quantity;
    private double maxPrice;
    private String currency;
    private long timeToLive;

    public NewLimitOrderCommand() {

    }

    public NewLimitOrderCommand(long sourceAddress, long eventTime, boolean buyAction, long qty, double maxPrice, String currency,
                                long timeToLive) {
        super(sourceAddress, eventTime);
        this.buyAction = buyAction;
        setQuantity(qty);
        setMaxPrice(maxPrice);
        setCurrency(currency);
        setTimeToLive(timeToLive);
    }


    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        this.buyAction = bytes.readBoolean();
        setQuantity(bytes.readLong());
        setMaxPrice(bytes.readDouble());
        setCurrency(bytes.readUtf8());
        setTimeToLive(bytes.readLong());
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeBoolean(buyAction);
        bytes.writeLong(quantity);
        bytes.writeDouble(maxPrice);
        bytes.writeUtf8(currency);
        bytes.writeLong(timeToLive);
    }

    @Override
    public int messageType() {
        return MessageTypes.NEW_LIMIT_ORDER_COMMAND;
    }


    public boolean isBuyAction() {
        return buyAction;
    }

    public boolean isSellAction() {
        return !buyAction;
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

    public String getCurrency() {
        return currency;
    }

    void setCurrency(String currency) {
        this.currency = notNullOrEmpty(currency);
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    void setTimeToLive(long timeToLive) {
        this.timeToLive = positive(timeToLive);
    }
}
