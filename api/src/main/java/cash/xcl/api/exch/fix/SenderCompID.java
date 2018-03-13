package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface SenderCompID {
    /**
     * Tag number for this field
     */
    int FIELD = 49;

    /**
     * @param senderCompID &gt; FIX TAG 49
     */
    void senderCompID(String senderCompID);

    default String senderCompID() {
        throw new UnsupportedOperationException();
    }
}
