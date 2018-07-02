package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import static cash.xcl.util.Validators.notNullOrEmpty;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class ApplicationMessageEvent extends SignedErrorMessage {
    public static final String ERROR = "ERROR";
    public static final String WARN = "WARN";
    public static final String INFO = "INFO";

    private String level;

    public ApplicationMessageEvent(long sourceAddress, long eventTime, SignedBinaryMessage orig, String reason, String level) {
        super(sourceAddress, eventTime, orig, reason);
        this.level = level;
    }

    public ApplicationMessageEvent(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, String origProtocol, String origMessageType, String reason, String level) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime, origProtocol, origMessageType, reason);
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
    protected void readMarshallable2(BytesIn<?> bytes) {
        super.readMarshallable2(bytes);
        level = bytes.readUtf8();
    }

    @Override
    public int intMessageType() {
        return MessageTypes.APPLICATION_MESSAGE_EVENT;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        super.writeMarshallable2(bytes);
        bytes.writeUtf8(level);
    }
}
