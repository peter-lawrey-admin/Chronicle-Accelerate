package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java)
 */
public interface Symbol {
    /**
     * Tag number for this field
     */
    int FIELD = 55;

    /**
     * @param symbol &gt; FIX TAG 55
     */
    void symbol(String symbol);

    default String symbol() {
        throw new UnsupportedOperationException();
    }
}
