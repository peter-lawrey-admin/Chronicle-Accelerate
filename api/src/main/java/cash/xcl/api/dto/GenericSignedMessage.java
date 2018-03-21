package cash.xcl.api.dto;

import cash.xcl.api.DtoParser;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class GenericSignedMessage extends SignedMessage {
    public GenericSignedMessage() {
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {

    }

    @Override
    public int messageType() {
        return sigAndMsg().readUnsignedByte(DtoParser.MESSAGE_OFFSET);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {

    }
}
