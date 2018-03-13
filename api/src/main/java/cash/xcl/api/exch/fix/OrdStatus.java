package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface OrdStatus {
    /**
     * Tag number for this field
     */
    int FIELD = 39;

    char NEW = '0';

    char PARTIALLY_FILLED = '1';

    char FILLED = '2';

    char DONE_FOR_DAY = '3';

    char CANCELED = '4';

    char REPLACED = '5';

    char PENDING_CANCEL = '6';

    char STOPPED = '7';

    char REJECTED = '8';

    char SUSPENDED = '9';

    char PENDING_NEW = 'A';

    char CALCULATED = 'B';

    char EXPIRED = 'C';

    char ACCEPTED_FOR_BIDDING = 'D';

    char PENDING_REPLACE = 'E';

    static String asString(char value) {
        switch (value) {
            case NEW:
                return "NEW";
            case PARTIALLY_FILLED:
                return "PARTIALLY_FILLED";
            case FILLED:
                return "FILLED";
            case DONE_FOR_DAY:
                return "DONE_FOR_DAY";
            case CANCELED:
                return "CANCELED";
            case REPLACED:
                return "REPLACED";
            case PENDING_CANCEL:
                return "PENDING_CANCEL";
            case STOPPED:
                return "STOPPED";
            case REJECTED:
                return "REJECTED";
            case SUSPENDED:
                return "SUSPENDED";
            case PENDING_NEW:
                return "PENDING_NEW";
            case CALCULATED:
                return "CALCULATED";
            case EXPIRED:
                return "EXPIRED";
            case ACCEPTED_FOR_BIDDING:
                return "ACCEPTED_FOR_BIDDING";
            case PENDING_REPLACE:
                return "PENDING_REPLACE";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param ordStatus &gt; FIX TAG 39
     */
    void ordStatus(char ordStatus);

    default char ordStatus() {
        throw new UnsupportedOperationException();
    }
}
