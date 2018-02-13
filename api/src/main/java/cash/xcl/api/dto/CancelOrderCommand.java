package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class CancelOrderCommand extends SignedMessage {

    private String orderId;
    //private Order order;

    public CancelOrderCommand(long sourceAddress, long eventTime, String orderId) {
        super(sourceAddress, eventTime);
        this.orderId = orderId;
    }

    public CancelOrderCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
    }

    @Override
    public int messageType() {

        return MessageTypes.CANCEL_ORDER_COMMAND;
    }

}
