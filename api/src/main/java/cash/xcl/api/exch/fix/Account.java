package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface Account {
    /**
     * Tag number for this field
     */
    int FIELD = 1;

    /**
     * @param account &gt; FIX TAG 1
     */
    void account(String account);

    default String account() {
        throw new UnsupportedOperationException();
    }
}
