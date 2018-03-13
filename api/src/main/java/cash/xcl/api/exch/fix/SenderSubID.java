package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java)
 */
public interface SenderSubID {
    /**
     * Tag number for this field
     */
    int FIELD = 50;

    /**
     * @param senderSubID &gt; FIX TAG 50
     */
    void senderSubID(String senderSubID);

    default String senderSubID() {
        throw new UnsupportedOperationException();
    }
}
