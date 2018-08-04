package im.xcl.platform.api;

import net.openhft.chronicle.bytes.*;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.TimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

import java.util.function.LongFunction;

public class AbstractSignedMessage<T extends AbstractSignedMessage<T>> extends AbstractBytesMarshallable implements SignedMessage {
    private static final int LENGTH = 0;
    private static final int FORMAT = LENGTH + Integer.BYTES;
    private static final int SIGNATURE = FORMAT + Integer.BYTES;
    private static final int PROTOCOL = SIGNATURE + Long.BYTES;
    private static final int MESSAGE_TYPE = PROTOCOL + Short.BYTES;
    private static final int MESSAGE_START = MESSAGE_TYPE + Short.BYTES;

    // for writing to a new set of bytes
    private transient Bytes tempBytes = Bytes.allocateElasticDirect(4L << 10);
    // for reading an existing Bytes
    private transient PointerBytesStore readPointer = BytesStore.nativePointer();
    private transient Bytes<Void> bytes = readPointer.bytesForRead();

    private transient boolean signed = false;

    private short protocol, messageType;
    private long address, timestampUS;

    protected AbstractSignedMessage(int protocol, int messageType) {
        assert protocol == (short) protocol;
        assert messageType == (short) messageType;
        this.protocol = (short) protocol;
        this.messageType = (short) messageType;
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {
        readPointer.set(bytes.addressForRead(bytes.readPosition()), bytes.readRemaining());
        this.bytes.readPosition(MESSAGE_START);
        this.bytes.readLimit(bytes.readRemaining());
        super.readMarshallable(this.bytes);
        signed = true;
    }

    public void reset() {
        signed = false;
        // address = timestampUS = 0; set by super.reset();
        super.reset();
    }

    @Override
    public boolean signed() {
        return signed;
    }

    @Override
    public void writeMarshallable(BytesOut bytes) {
        assert signed();
        bytes.write(this.bytes, 0, this.bytes.readLimit());
    }

    protected void writeMarshallable0(BytesOut bytes) {
        super.writeMarshallable(bytes);
    }

    @Override
    public long address() {
        return address;
    }

    public T address(long address) {
        assert !signed();
        this.address = address;
        return (T) this;
    }

    @Override
    public long timestampUS() {
        return timestampUS;
    }

    public T timestampUS(long timestampUS) {
        assert !signed();
        this.timestampUS = timestampUS;
        return (T) this;
    }

    /**
     * Signifies this message contains it's own public key.
     *
     * @return the public key for this address.
     */
    @Override
    public BytesStore publicKey() {
        return NoBytesStore.noBytesStore();
    }

    public boolean hasPublicKey() {
        return false;
    }

    public T publicKey(BytesStore key) {
        assert false;
        return (T) this;
    }

    public int format() {
        return '?' * 0x01010101; // TBD
    }

    @Override
    public void sign(BytesStore secretKey) {
        UniqueMicroTimeProvider timeProvider = UniqueMicroTimeProvider.INSTANCE;
        sign(secretKey, timeProvider);
    }

    public void sign(BytesStore secretKey, TimeProvider timeProvider) {
        assert !signed();

        if (hasPublicKey())
            publicKey(secretKey);

        address = secretKey.readLong(secretKey.readRemaining() - Long.BYTES);
        timestampUS = timeProvider.currentTimeMicros();

        tempBytes.clear();
        tempBytes.writeInt(0);
        tempBytes.writeInt(format());
        long signatureStart = tempBytes.writePosition();
        tempBytes.writeSkip(Ed25519.SIGNATURE_LENGTH);
        writeMarshallable0(tempBytes);
        long length = tempBytes.readRemaining();
        tempBytes.writeUnsignedInt(LENGTH, length);
        tempBytes.readPosition(signatureStart);
        Ed25519.sign(tempBytes, secretKey);
        signed = true;
        readPointer.set(tempBytes.addressForRead(0), length);
        bytes.writeLimit(length)
                .readPositionRemaining(0, length);
    }

    public String toHexString() {
        HexDumpBytes dump = new HexDumpBytes();
        dump.comment("length").writeUnsignedInt(bytes.readUnsignedInt(LENGTH));
        StringBuilder formatStr = new StringBuilder("format ");
        for (int i = FORMAT; i < FORMAT + Integer.BYTES; i++)
            formatStr.append((char) bytes.readUnsignedByte(i));
        dump.comment(formatStr).writeUnsignedInt(bytes.readUnsignedInt(FORMAT));
        dump.comment("signature start").write(bytes, (long) SIGNATURE, Ed25519.SIGNATURE_LENGTH);
        dump.comment("signature end");
        writeMarshallable0(dump);
        String text = dump.toHexString();
        dump.release();
        return text;
    }

    public boolean verify(LongFunction<BytesStore> addressToPublickKey) {
        BytesStore publicKey = hasPublicKey()
                ? publicKey()
                : addressToPublickKey.apply(address());
        if (publicKey == null || publicKey.readRemaining() != Ed25519.SIGNATURE_LENGTH)
            return false;

        bytes.readPosition(SIGNATURE);
        bytes.readLimit(readPointer.readLimit());
        return Ed25519.verify(bytes, publicKey);
    }

    public short protocol() {
        return protocol;
    }

    public T protocol(short protocol) {
        this.protocol = protocol;
        return (T) this;
    }

    public short messageType() {
        return messageType;
    }

    public T messageType(short messageType) {
        this.messageType = messageType;
        return (T) this;
    }
}
