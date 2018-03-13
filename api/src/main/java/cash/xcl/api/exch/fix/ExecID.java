package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface ExecID {
    /**
     * Tag number for this field
     */
    int FIELD = 17;

    /**
     * @param execID &gt; FIX TAG 17
     */
    void execID(String execID);

    default String execID() {
        throw new UnsupportedOperationException();
    }
}
