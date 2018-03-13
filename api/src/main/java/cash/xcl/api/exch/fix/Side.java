package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface Side {
    /**
     * Tag number for this field
     */
    int FIELD = 54;

    char BUY = '1';

    char SELL = '2';

    char BUY_MINUS = '3';

    char SELL_PLUS = '4';

    char SELL_SHORT = '5';

    char SELL_SHORT_EXEMPT = '6';

    char D = '7';

    char CROSS = '8';

    char CROSS_SHORT = '9';

    static String asString(char value) {
        switch (value) {
            case BUY:
                return "BUY";
            case SELL:
                return "SELL";
            case BUY_MINUS:
                return "BUY_MINUS";
            case SELL_PLUS:
                return "SELL_PLUS";
            case SELL_SHORT:
                return "SELL_SHORT";
            case SELL_SHORT_EXEMPT:
                return "SELL_SHORT_EXEMPT";
            case D:
                return "D";
            case CROSS:
                return "CROSS";
            case CROSS_SHORT:
                return "CROSS_SHORT";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param side &gt; FIX TAG 54
     */
    void side(char side);

    default char side() {
        throw new UnsupportedOperationException();
    }
}
