package cash.xcl.api;

import net.openhft.chronicle.bytes.Bytes;

public interface DtoParser<M> {
    int PROTOCOL_OFFSET = 80;
    int MESSAGE_OFFSET = 82;

    static int protocol(Bytes<?> bytes) {
        return bytes.readUnsignedShort(bytes.readPosition() + PROTOCOL_OFFSET);
    }

    static int messageType(Bytes<?> bytes) {
        return bytes.readUnsignedShort(bytes.readPosition() + MESSAGE_OFFSET);
    }

    void parseOne(Bytes<?> bytes, M messages);
}
