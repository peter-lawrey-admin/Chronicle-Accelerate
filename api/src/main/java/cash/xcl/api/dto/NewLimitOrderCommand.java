package cash.xcl.api.dto;


import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;


// FIXME needs reviewing/completing
public class NewLimitOrderCommand extends SignedMessage {

    private LimitOrder limitOrder;


    public NewLimitOrderCommand(long sourceAddress, long eventTime,
                                LimitOrder limitOrder) {
        super(sourceAddress, eventTime);
        this.limitOrder = limitOrder;
    }

    public NewLimitOrderCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {


    }

    @Override
    public int messageType() {

        return MethodIds.NEW_LIMIT_ORDER_COMMAND;
    }

    public NewLimitOrderCommand(LimitOrder limitOrder) {
        this.limitOrder = limitOrder;
    }
}
