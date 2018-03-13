package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface SecurityID {
    /**
     * Tag number for this field
     */
    int FIELD = 48;

    /**
     * @param securityID &gt; FIX TAG 48
     */
    void securityID(String securityID);

    default String securityID() {
        throw new UnsupportedOperationException();
    }
}
