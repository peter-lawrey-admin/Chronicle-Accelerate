package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class SubscriptionCommand extends SignedMessage {
    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    protected int messageType() {
        return MethodIds.SUBSCRIPTION_COMMAND;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {

    }
}
