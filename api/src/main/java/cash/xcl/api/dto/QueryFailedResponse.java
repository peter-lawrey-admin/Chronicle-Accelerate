package cash.xcl.api.dto;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class QueryFailedResponse extends SignedErrorMessage {

    public QueryFailedResponse(long sourceAddress, long eventTime, SignedBinaryMessage orig, String reason) {
        super(sourceAddress, eventTime, orig, reason);
    }

    public QueryFailedResponse(long sourceAddress, long eventTime, long origSourceAddress, long origEventTime, String origProtocol, String origMessageType, String reason) {
        super(sourceAddress, eventTime, origSourceAddress, origEventTime, origProtocol, origMessageType, reason);
    }

    public QueryFailedResponse() {

    }

    @Override
    public int intMessageType() {
        return MessageTypes.QUERY_FAILED_RESPONSE;
    }
}
