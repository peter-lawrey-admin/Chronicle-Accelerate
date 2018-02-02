package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

// TODO
public class SubscriptionSuccess extends SignedMessage {
    private SubscriptionCommand subscriptionCommand;

    public SubscriptionSuccess(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }


    public SubscriptionSuccess() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
    }

    @Override
    public int messageType() {
        return MethodIds.SUBSCRIPTION_SUCCESS_EVENT;
    }
}
