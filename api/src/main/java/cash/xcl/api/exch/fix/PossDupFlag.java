package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface PossDupFlag {
    /**
     * Tag number for this field
     */
    int FIELD = 43;

    /**
     * @param possDupFlag &gt; FIX TAG 43
     */
    void possDupFlag(char possDupFlag);

    default char possDupFlag() {
        throw new UnsupportedOperationException();
    }
}
