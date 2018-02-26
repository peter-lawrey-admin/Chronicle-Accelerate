package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class CancelOrderCommand extends SignedMessage {

    private String clientOrderId;
    //private Order order;

    public CancelOrderCommand(long sourceAddress, long eventTime, String clientOrderId) {
        super(sourceAddress, eventTime);
        this.clientOrderId = clientOrderId;
    }

    public CancelOrderCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
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
