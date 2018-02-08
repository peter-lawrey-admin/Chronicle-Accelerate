package cash.xcl.api.dto;

/**
 * A generic application message has been reported.  These should be used as little as possible as they cannot be easily processed downstream.
 */
public class QueryFailedResponse extends SignedErrorMessage {

    public QueryFailedResponse(long sourceAddress, long eventTime, int origMessageType, long origSourceAddress, long origSourceEventTime, String reason) {
        super(sourceAddress, eventTime, origMessageType, origSourceAddress, origSourceEventTime, reason);
    }

    public QueryFailedResponse() {

    }

    @Override
    public int messageType() {
        return MessageTypes.QUERY_FAILED_RESPONSE;
    }
}
