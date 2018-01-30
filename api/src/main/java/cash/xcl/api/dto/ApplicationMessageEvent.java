package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

import static cash.xcl.api.dto.Validators.notNullOrEmpty;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class ApplicationMessageEvent extends SignedMessage {
    public static final String ERROR = "ERROR";
    public static final String WARN = "WARN";
    public static final String INFO = "INFO";

    private String level;
    private String reason;

    public ApplicationMessageEvent(long serviceAddress, long eventTime, String level, String reason) {
        super(serviceAddress, eventTime);
        setLevel(level);
        setReason(reason);
    }

    public ApplicationMessageEvent() {

    }

    public String getLevel() {
        return level;
    }

    private void setLevel(String level) {
        this.level = notNullOrEmpty(level);
    }

    public String getReason() {
        return reason;
    }

    private void setReason(String reason) {
        this.reason = notNullOrEmpty(reason);
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        level = bytes.readUtf8();
        reason = bytes.readUtf8();
    }

    @Override
    protected int messageType() {
        return MethodIds.APPLICATION_MESSAGE_EVENT;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeUtf8(level);
        bytes.writeUtf8(reason);
    }
}
