package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public abstract class SignedTracedMessage extends SignedMessage {

    private long origSourceAddress;
    private long origEventTime;

    public SignedTracedMessage(long sourceAddress, long eventTime, SignedMessage orig) {
        this(sourceAddress, eventTime, orig.sourceAddress(), orig.eventTime());
    }

    protected SignedTracedMessage(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime) {
        super(sourceAddress, eventTime);
        this.origSourceAddress = origSourceAddress;
        this.origEventTime = origEventTime;
    }

    public SignedTracedMessage() {

    }

    public long origSourceAddress() {
        return origSourceAddress;
    }

    public SignedTracedMessage origSourceAddress(long origSourceAddress) {
        this.origSourceAddress = origSourceAddress;
        return this;
    }

    public long origEventTime() {
        return origEventTime;
    }

    public SignedTracedMessage origEventTime(long origSourceEventTime) {
        this.origEventTime = origSourceEventTime;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        origSourceAddress = bytes.readLong();
        origEventTime = bytes.readLong();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(origSourceAddress);
        bytes.writeLong(origEventTime);
    }
}
