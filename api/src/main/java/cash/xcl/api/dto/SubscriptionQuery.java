package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class SubscriptionQuery extends SignedBinaryMessage {
    public SubscriptionQuery(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    public SubscriptionQuery() {

    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {

    }

    @Override
    public int intMessageType() {
        return MessageTypes.SUBSCRIPTION_QUERY;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {

    }
}
