package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// TODO
public class SubscriptionSuccessResponse extends SignedMessage {

    private SubscriptionQuery subscriptionQuery;

    public SubscriptionSuccessResponse(long sourceAddress, long eventTime, SubscriptionQuery subscriptionQuery) {
        super(sourceAddress, eventTime);
        this.subscriptionQuery = subscriptionQuery;
    }

    public SubscriptionSuccessResponse() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
    }

    @Override
    public int messageType() {

        return MessageTypes.SUBSCRIPTION_SUCCESS_RESPONSE;
    }
}
