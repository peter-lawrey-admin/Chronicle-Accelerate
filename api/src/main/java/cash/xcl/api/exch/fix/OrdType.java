package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface OrdType {
    /**
     * Tag number for this field
     */
    int FIELD = 40;

    char MARKET = '1';

    char LIMIT = '2';

    char STOP = '3';

    char STOP_LIMIT = '4';

    char MARKET_ON_CLOSE = '5';

    char WITH_OR_WITHOUT = '6';

    char LIMIT_OR_BETTER = '7';

    char LIMIT_WITH_OR_WITHOUT = '8';

    char ON_BASIS = '9';

    char ON_CLOSE = 'A';

    char LIMIT_ON_CLOSE = 'B';

    char FOREX_MARKET = 'C';

    char PREVIOUSLY_QUOTED = 'D';

    char PREVIOUSLY_INDICATED = 'E';

    char FOREX_LIMIT = 'F';

    char FOREX_SWAP = 'G';

    char FOREX_PREVIOUSLY_QUOTED = 'H';

    char FUNARI = 'I';

    char PEGGED = 'P';

    static String asString(char value) {
        switch (value) {
            case MARKET:
                return "MARKET";
            case LIMIT:
                return "LIMIT";
            case STOP:
                return "STOP";
            case STOP_LIMIT:
                return "STOP_LIMIT";
            case MARKET_ON_CLOSE:
                return "MARKET_ON_CLOSE";
            case WITH_OR_WITHOUT:
                return "WITH_OR_WITHOUT";
            case LIMIT_OR_BETTER:
                return "LIMIT_OR_BETTER";
            case LIMIT_WITH_OR_WITHOUT:
                return "LIMIT_WITH_OR_WITHOUT";
            case ON_BASIS:
                return "ON_BASIS";
            case ON_CLOSE:
                return "ON_CLOSE";
            case LIMIT_ON_CLOSE:
                return "LIMIT_ON_CLOSE";
            case FOREX_MARKET:
                return "FOREX_MARKET";
            case PREVIOUSLY_QUOTED:
                return "PREVIOUSLY_QUOTED";
            case PREVIOUSLY_INDICATED:
                return "PREVIOUSLY_INDICATED";
            case FOREX_LIMIT:
                return "FOREX_LIMIT";
            case FOREX_SWAP:
                return "FOREX_SWAP";
            case FOREX_PREVIOUSLY_QUOTED:
                return "FOREX_PREVIOUSLY_QUOTED";
            case FUNARI:
                return "FUNARI";
            case PEGGED:
                return "PEGGED";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param ordType &gt; FIX TAG 40
     */
    void ordType(char ordType);

    default char ordType() {
        throw new UnsupportedOperationException();
    }
}
