package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface SettlmntTyp {
    /**
     * Tag number for this field
     */
    int FIELD = 63;

    char REGULAR = '0';

    char CASH = '1';

    char NEXT_DAY = '2';

    char TPLUS2 = '3';

    char TPLUS3 = '4';

    char TPLUS4 = '5';

    char FUTURE = '6';

    char WHEN_ISSUED = '7';

    char SELLERS_OPTION = '8';

    char TPLUS5 = '9';

    static String asString(char value) {
        switch (value) {
            case REGULAR:
                return "REGULAR";
            case CASH:
                return "CASH";
            case NEXT_DAY:
                return "NEXT_DAY";
            case TPLUS2:
                return "TPLUS2";
            case TPLUS3:
                return "TPLUS3";
            case TPLUS4:
                return "TPLUS4";
            case FUTURE:
                return "FUTURE";
            case WHEN_ISSUED:
                return "WHEN_ISSUED";
            case SELLERS_OPTION:
                return "SELLERS_OPTION";
            case TPLUS5:
                return "TPLUS5";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param settlmntTyp &gt; FIX TAG 63
     */
    void settlmntTyp(char settlmntTyp);

    default char settlmntTyp() {
        throw new UnsupportedOperationException();
    }
}
