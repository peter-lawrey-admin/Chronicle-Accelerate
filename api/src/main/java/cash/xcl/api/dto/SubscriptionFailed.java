package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

// TODO
public class SubscriptionFailed extends SignedMessage {
    private SubscriptionCommand subscriptionCommand;

    public SubscriptionFailed(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }


    public SubscriptionFailed() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
    }

    @Override
    protected int messageType() {
        return MethodIds.SUBSCRIPTION_FAILED_EVENT;
    }
}
