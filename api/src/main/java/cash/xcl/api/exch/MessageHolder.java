package cash.xcl.api.exch;

import cash.xcl.api.dto.SignedMessage;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesMarshallable;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.Marshallable;

public class MessageHolder<T extends Marshallable & BytesMarshallable> extends SignedMessage {
    T message;

    MessageHolder() {

    }

    MessageHolder(T message) {
        this.message = message;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        message.readMarshallable(bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        message.writeMarshallable(bytes);
    }

    @Override
    public int messageType() {
        return 0;
    }

}
