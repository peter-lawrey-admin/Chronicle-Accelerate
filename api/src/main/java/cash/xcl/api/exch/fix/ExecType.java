package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface ExecType {
    /**
     * Tag number for this field
     */
    int FIELD = 150;

    char NEW = '0';

    char PARTIAL_FILL = '1';

    char FILL = '2';

    char DONE_FOR_DAY = '3';

    char CANCELED = '4';

    char REPLACE = '5';

    char PENDING_CANCEL = '6';

    char STOPPED = '7';

    char REJECTED = '8';

    char SUSPENDED = '9';

    char PENDING_NEW = 'A';

    char CALCULATED = 'B';

    char EXPIRED = 'C';

    char RESTATED = 'D';

    char PENDING_REPLACE = 'E';

    static String asString(char value) {
        switch (value) {
            case NEW:
                return "NEW";
            case PARTIAL_FILL:
                return "PARTIAL_FILL";
            case FILL:
                return "FILL";
            case DONE_FOR_DAY:
                return "DONE_FOR_DAY";
            case CANCELED:
                return "CANCELED";
            case REPLACE:
                return "REPLACE";
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
            case RESTATED:
                return "RESTATED";
            case PENDING_REPLACE:
                return "PENDING_REPLACE";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param execType &gt; FIX TAG 150
     */
    void execType(char execType);

    default char execType() {
        throw new UnsupportedOperationException();
    }
}
