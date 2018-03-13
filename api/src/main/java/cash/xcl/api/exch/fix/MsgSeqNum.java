package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface MsgSeqNum {
    /**
     * Tag number for this field
     */
    int FIELD = 34;

    /**
     * @param msgSeqNum &gt; FIX TAG 34
     */
    void msgSeqNum(long msgSeqNum);

    default long msgSeqNum() {
        throw new UnsupportedOperationException();
    }
}
