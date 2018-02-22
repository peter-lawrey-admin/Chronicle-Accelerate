package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class OrderClosedEvent extends SignedMessage {
    public static enum REASON {
        USER_REQUEST((byte) 0), TIME_OUT((byte) 32);
        private byte value;

        REASON(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

    }

    private long orderSourceAddress;
    private long orderEventTime;
    private REASON reason;

    public OrderClosedEvent() {

    }

    public OrderClosedEvent(long sourceAddress, long eventTime, long orderSourceAddress, long orderEventTime, REASON reason) {
        super(sourceAddress, eventTime);
        this.orderSourceAddress = orderSourceAddress;
        this.orderEventTime = orderEventTime;
        this.reason = reason;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        orderSourceAddress = bytes.readLong();
        orderEventTime = bytes.readLong();
        reason = (bytes.readByte() == REASON.USER_REQUEST.getValue() ? REASON.USER_REQUEST : REASON.TIME_OUT);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(orderSourceAddress);
        bytes.writeLong(orderEventTime);
        bytes.writeByte(reason.getValue());
    }

    @Override
    public int messageType() {
        return MessageTypes.ORDER_CLOSED_EVENT;
    }

    public long getOrderSourceAddress() {
        return orderSourceAddress;
    }

    public long getOrderEventTime() {
        return orderEventTime;
    }

    public REASON getReason() {
        return reason;
    }

}
