package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class CancelOrderCommand extends SignedMessage {

    private String clientOrderId;
    //private Order order;
    private long orderTime;

    public CancelOrderCommand(long sourceAddress, long eventTime, String clientOrderId) {
        super(sourceAddress, eventTime);
        this.clientOrderId = clientOrderId;
        this.orderTime = orderTime;
    }

    public CancelOrderCommand() {

    }

    public long getOrderTime() {
        return orderTime;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        this.clientOrderId = bytes.readUtf8();
        this.orderTime = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUtf8(clientOrderId);
        bytes.writeLong(orderTime);
    }

    public String clientOrderId() {
        return clientOrderId;
    }

    public CancelOrderCommand clientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
        return this;
    }

    @Override
    public int messageType() {
        return MessageTypes.CANCEL_ORDER_COMMAND;
    }

}
