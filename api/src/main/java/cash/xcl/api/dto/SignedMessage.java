package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.Marshallable;

import java.nio.ByteBuffer;

public interface SignedMessage extends Marshallable {
    boolean hasSignature();

    Bytes<ByteBuffer> sigAndMsg();

    SignedMessage sigAndMsg(Bytes<ByteBuffer> sigAndMsg);

    long sourceAddress();

    SignedMessage sourceAddress(long sourceAddress);

    long eventTime();

    SignedMessage eventTime(long eventTime);

    String protocol();

    String messageType();
}
