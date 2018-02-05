package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

import static cash.xcl.api.dto.Validators.notNullOrEmpty;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class ApplicationMessageEvent extends SignedErrorMessage {
    public static final String ERROR = "ERROR";
    public static final String WARN = "WARN";
    public static final String INFO = "INFO";

    private String level;

    public ApplicationMessageEvent(long sourceAddress, long eventTime, int origMessageType, long origSourceAddress, long origSourceEventTime, String reason, String level) {
        super(sourceAddress, eventTime, origMessageType, origSourceAddress, origSourceEventTime, reason);
        level(level);
    }

    public ApplicationMessageEvent() {

    }

    public String level() {
        return level;
    }

    public ApplicationMessageEvent level(String level) {
        this.level = notNullOrEmpty(level);
        ;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        super.readMarshallable2(bytes);
        level = bytes.readUtf8();
    }

    @Override
    public int messageType() {
        return MethodIds.APPLICATION_MESSAGE_EVENT;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(level);
    }
}
