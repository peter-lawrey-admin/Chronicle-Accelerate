package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.api.dto.Validators.notNullOrEmpty;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class SignedErrorMessage extends SignedTracedMessage {

    private int origProtocol;
    private int origMessageType;
    private String reason;

    public SignedErrorMessage(long sourceAddress, long eventTime, SignedMessage orig, String reason) {
        super(sourceAddress, eventTime, orig);
        this.origProtocol = orig.protocol();
        this.origMessageType = orig.messageType();
        this.reason = reason;
    }

    public SignedErrorMessage(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, int origProtocol, int origMessageType, String reason) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime);
        this.origProtocol = origProtocol;
        this.origMessageType = origMessageType;
        this.reason = reason;
    }

    public SignedErrorMessage() {

    }

    public SignedErrorMessage messageType(int messageType) {
        this.origMessageType = messageType;
        return this;
    }

    public String reason() {
        return reason;
    }

    public SignedErrorMessage reason(String reason) {
        this.reason = reason;
        return this;
    }

    public String getReason() {
        return reason;
    }

    private void setReason(String reason) {
        this.reason = notNullOrEmpty(reason);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        origProtocol = bytes.readUnsignedShort();
        origMessageType = bytes.readUnsignedShort();
        reason = bytes.readUtf8();
    }

    @Override
    public int messageType() {
        return MessageTypes.COMMAND_FAILED_EVENT;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUnsignedShort(origProtocol);
        bytes.writeUnsignedShort(origMessageType);
        bytes.writeUtf8(reason);
    }
}
