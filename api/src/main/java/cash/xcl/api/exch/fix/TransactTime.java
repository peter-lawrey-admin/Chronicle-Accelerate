package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface TransactTime {
    /**
     * Tag number for this field
     */
    int FIELD = 60;

    /**
     * @param transactTime &gt; FIX TAG 60
     */
    void transactTime(long transactTime);

    default long transactTime() {
        throw new UnsupportedOperationException();
    }
}
