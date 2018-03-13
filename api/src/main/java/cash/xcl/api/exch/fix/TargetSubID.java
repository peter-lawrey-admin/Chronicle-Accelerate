package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java)
 */
public interface TargetSubID {
    /**
     * Tag number for this field
     */
    int FIELD = 57;

    /**
     * @param targetSubID &gt; FIX TAG 57
     */
    void targetSubID(String targetSubID);

    default String targetSubID() {
        throw new UnsupportedOperationException();
    }
}
