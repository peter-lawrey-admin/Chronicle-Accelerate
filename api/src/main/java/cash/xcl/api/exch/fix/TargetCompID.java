package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface TargetCompID {
    /**
     * Tag number for this field
     */
    int FIELD = 56;

    /**
     * @param targetCompID &gt; FIX TAG 56
     */
    void targetCompID(String targetCompID);

    default String targetCompID() {
        throw new UnsupportedOperationException();
    }
}
