package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public abstract class SignedMessage extends AbstractBytesMarshallable {
    private transient Bytes sigAndMsg;
    private long sourceAddress;
    private long eventTime;
    private int protocol;

    protected Bytes sigAndMsg() {
        return sigAndMsg == null ? sigAndMsg = Bytes.allocateElasticDirect() : sigAndMsg;
    }

    @Override
    public final void readMarshallable(BytesIn bytes) throws IORuntimeException {
        sigAndMsg().clear().write((BytesStore) bytes);
        bytes.readSkip(Ed25519.SIGANTURE_LENGTH);
        sourceAddress = bytes.readLong();
        eventTime = bytes.readLong();
        protocol = bytes.readUnsignedByte();
        int messageType = bytes.readUnsignedByte();
        assert messageType == messageType();
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
        tempBytes.writeUnsignedByte(protocol);
        tempBytes.writeUnsignedByte(messageType());
        tempBytes.writeUnsignedShort(0); // padding.
        writeMarshallable2(tempBytes);
        Ed25519.sign(sigAndMsg, tempBytes, secretKey);
    }

    protected abstract int messageType();

    protected abstract void writeMarshallable2(Bytes bytes);

    public boolean hasSignature() {
        return sigAndMsg != null && sigAndMsg.readRemaining() > Ed25519.SIGANTURE_LENGTH;
    }

    @Override
    public void reset() {
        sigAndMsg().clear();
        sourceAddress = 0;
        eventTime = 0;
        protocol = 0;
    }


}
