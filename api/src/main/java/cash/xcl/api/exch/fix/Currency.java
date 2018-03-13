package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface Currency {
    /**
     * Tag number for this field
     */
    int FIELD = 15;

    /**
     * @param currency &gt; FIX TAG 15
     */
    void currency(String currency);

    default String currency() {
        throw new UnsupportedOperationException();
    }
}
