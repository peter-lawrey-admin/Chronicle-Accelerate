package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class SubscriptionQuery extends SignedMessage {
    public SubscriptionQuery(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    public SubscriptionQuery() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    public int messageType() {
        return MethodIds.SUBSCRIPTION_QUERY;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {

    }
}
