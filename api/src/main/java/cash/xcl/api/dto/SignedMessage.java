package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

import java.nio.ByteBuffer;

public abstract class SignedMessage extends AbstractBytesMarshallable {
    private transient Bytes<ByteBuffer> sigAndMsg;
    private long sourceAddress;
    private long eventTime;

    protected SignedMessage() {
    }

    protected SignedMessage(long sourceAddress, long eventTime) {
        this.sourceAddress = sourceAddress;
        this.eventTime = eventTime;
    }

    @Override
    public final void readMarshallable(BytesIn bytes) throws IORuntimeException {
        sigAndMsg().clear().write((BytesStore) bytes);
        bytes.readSkip(Ed25519.SIGNATURE_LENGTH);
        sourceAddress = bytes.readLong();
        eventTime = bytes.readLong();
        int protocol = bytes.readUnsignedByte();
        assert bytes.lenient() || protocol == 1;
        int messageType = bytes.readUnsignedByte();
        assert bytes.lenient() || messageType == messageType();
        int padding = bytes.readUnsignedShort();
        readMarshallable2(bytes);
    }

    protected abstract void readMarshallable2(BytesIn bytes);

    @Override
    public final void writeMarshallable(BytesOut bytes) {
        if (hasSignature()) {
            bytes.write(sigAndMsg);
            return;
        }
        throw new IllegalStateException("Need to add a signature first");
    }

    public void sign(Bytes tempBytes, long sourceAddress, Bytes secretKey) {
        if (this.sourceAddress == 0)
            this.sourceAddress = sourceAddress;
        else if (this.sourceAddress != sourceAddress)
            throw new IllegalArgumentException("Cannot change the source address");
        tempBytes.clear();
        tempBytes.writeLong(sourceAddress);
        tempBytes.writeLong(eventTime);
        tempBytes.writeUnsignedByte(1);
        tempBytes.writeUnsignedByte(messageType());
        tempBytes.writeUnsignedShort(0); // padding.
        writeMarshallable2(tempBytes);
        if (sigAndMsg == null)
            sigAndMsg = Bytes.elasticByteBuffer();
        else
            sigAndMsg.clear();
        Ed25519.sign(sigAndMsg, tempBytes, secretKey);
    }

    public abstract int messageType();

    protected abstract void writeMarshallable2(Bytes bytes);

    @Override
    public void reset() {
        sigAndMsg().clear();
        sourceAddress = 0;
        eventTime = 0;
    }

    public boolean hasSignature() {
        return sigAndMsg != null && sigAndMsg.readRemaining() > Ed25519.SIGNATURE_LENGTH;
    }

    public Bytes<ByteBuffer> sigAndMsg() {
        return sigAndMsg == null ? sigAndMsg = Bytes.elasticByteBuffer() : sigAndMsg;
    }

    public SignedMessage sigAndMsg(Bytes<ByteBuffer> sigAndMsg) {
        this.sigAndMsg = sigAndMsg;
        return this;
    }

    public long sourceAddress() {
        return sourceAddress;
    }

    public SignedMessage sourceAddress(long sourceAddress) {
        this.sourceAddress = sourceAddress;
        return this;
    }

    public long eventTime() {
        return eventTime;
    }

    public SignedMessage eventTime(long eventTime) {
        this.eventTime = eventTime;
        return this;
    }
}
