package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface ExecTransType {
    /**
     * Tag number for this field
     */
    int FIELD = 20;

    char NEW = '0';

    char CANCEL = '1';

    char CORRECT = '2';

    char STATUS = '3';

    static String asString(char value) {
        switch (value) {
            case NEW:
                return "NEW";
            case CANCEL:
                return "CANCEL";
            case CORRECT:
                return "CORRECT";
            case STATUS:
                return "STATUS";
            default:
                throw new IllegalArgumentException(value + " is not recognised");
        }
    }

    /**
     * @param execTransType &gt; FIX TAG 20
     */
    void execTransType(char execTransType);

    default char execTransType() {
        throw new UnsupportedOperationException();
    }
}
