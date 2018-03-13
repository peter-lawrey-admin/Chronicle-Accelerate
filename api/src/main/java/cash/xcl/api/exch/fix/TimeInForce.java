package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface TimeInForce {
    /**
     * Tag number for this field
     */
    int FIELD = 59;

    char DAY = '0';

    char GOOD_TILL_CANCEL = '1';

    char AT_THE_OPENING = '2';

    char IMMEDIATE_OR_CANCEL = '3';

    char FILL_OR_KILL = '4';

    char GOOD_TILL_CROSSING = '5';

    char GOOD_TILL_DATE = '6';

    static String asString(char value) {
        switch (value) {
            case DAY:
                return "DAY";
            case GOOD_TILL_CANCEL:
                return "GOOD_TILL_CANCEL";
            case AT_THE_OPENING:
                return "AT_THE_OPENING";
            case IMMEDIATE_OR_CANCEL:
                return "IMMEDIATE_OR_CANCEL";
            case FILL_OR_KILL:
                return "FILL_OR_KILL";
            case GOOD_TILL_CROSSING:
                return "GOOD_TILL_CROSSING";
            case GOOD_TILL_DATE:
                return "GOOD_TILL_DATE";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param timeInForce &gt; FIX TAG 59
     */
    void timeInForce(char timeInForce);

    default char timeInForce() {
        throw new UnsupportedOperationException();
    }
}
