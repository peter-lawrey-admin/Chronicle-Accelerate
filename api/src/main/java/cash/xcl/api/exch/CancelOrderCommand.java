package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class CancelOrderCommand extends SignedMessage {

    private long orderTime;

    public CancelOrderCommand(long sourceAddress, long eventTime, long orderTime) {
        super(sourceAddress, eventTime);
        this.orderTime = orderTime;
    }

    public CancelOrderCommand() {

    }

    public long getOrderTime() {
        return orderTime;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        this.orderTime = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(orderTime);
    }

    @Override
    public int messageType() {
        return MessageTypes.CANCEL_ORDER_COMMAND;
    }

}
