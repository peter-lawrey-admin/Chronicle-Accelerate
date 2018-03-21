package cash.xcl.api.dto;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class CommandFailedEvent extends SignedErrorMessage {

    public CommandFailedEvent(long sourceAddress, long eventTime, SignedMessage message, String reason) {
        super(sourceAddress, eventTime, message, reason);
    }

    public CommandFailedEvent(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, int origProtocol, int origMessageType, String reason) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime, origProtocol, origMessageType, reason);
    }

    public CommandFailedEvent() {

    }

    @Override
    public int messageType() {
        return MessageTypes.COMMAND_FAILED_EVENT;
    }
}
