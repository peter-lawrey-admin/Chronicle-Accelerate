package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface SettlCurrency {
    /**
     * Tag number for this field
     */
    int FIELD = 120;

    /**
     * @param settlCurrency &gt; FIX TAG 120
     */
    void settlCurrency(String settlCurrency);

    default String settlCurrency() {
        throw new UnsupportedOperationException();
    }
}
