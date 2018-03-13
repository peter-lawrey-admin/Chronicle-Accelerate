package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface HandlInst {
    /**
     * Tag number for this field
     */
    int FIELD = 21;

    char AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION = '1';

    char AUTOMATED_EXECUTION_ORDER_PUBLIC_BROKER_INTERVENTION_OK = '2';

    char MANUAL_ORDER_BEST_EXECUTION = '3';

    static String asString(char value) {
        switch (value) {
            case AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION:
                return "AUTOMATED_EXECUTION_ORDER_PRIVATE_NO_BROKER_INTERVENTION";
            case AUTOMATED_EXECUTION_ORDER_PUBLIC_BROKER_INTERVENTION_OK:
                return "AUTOMATED_EXECUTION_ORDER_PUBLIC_BROKER_INTERVENTION_OK";
            case MANUAL_ORDER_BEST_EXECUTION:
                return "MANUAL_ORDER_BEST_EXECUTION";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param handlInst &gt; FIX TAG 21
     */
    void handlInst(char handlInst);

    default char handlInst() {
        throw new UnsupportedOperationException();
    }
}
