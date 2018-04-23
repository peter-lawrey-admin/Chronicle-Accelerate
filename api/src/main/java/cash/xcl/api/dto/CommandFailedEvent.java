package cash.xcl.api.dto;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class CommandFailedEvent extends SignedErrorMessage {

    public CommandFailedEvent(SignedMessage message, String reason) {
        this(-1/* set when sent */, 0 /* set when sent */, message, reason);
    }

    public CommandFailedEvent(long sourceAddress, long eventTime, SignedMessage message, String reason) {
        super(sourceAddress, eventTime, message, reason);
    }

    public CommandFailedEvent(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, String origProtocol, String origMessageType, String reason) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime, origProtocol, origMessageType, reason);
    }

    public CommandFailedEvent() {

    }

    @Override
    public int intMessageType() {
        return MessageTypes.COMMAND_FAILED_EVENT;
    }
}
