package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface CreatedNS {
    /**
     * Tag number for this field
     */
    int FIELD = 9999;

    /**
     * @param createdNS &gt; FIX TAG 9999
     */
    void createdNS(long createdNS);

    default long createdNS() {
        throw new UnsupportedOperationException();
    }
}
