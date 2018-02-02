package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class SubscriptionCommand extends SignedMessage {
    public SubscriptionCommand(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    public SubscriptionCommand() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    public int messageType() {
        return MethodIds.SUBSCRIPTION_COMMAND;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {

    }
}
