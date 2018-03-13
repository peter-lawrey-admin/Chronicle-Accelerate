package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface PossResend {
    /**
     * Tag number for this field
     */
    int FIELD = 97;

    /**
     * @param possResend &gt; FIX TAG 97
     */
    void possResend(char possResend);

    default char possResend() {
        throw new UnsupportedOperationException();
    }
}
