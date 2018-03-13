package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface LastMkt {
    /**
     * Tag number for this field
     */
    int FIELD = 30;

    /**
     * @param lastMkt &gt; FIX TAG 30
     */
    void lastMkt(String lastMkt);

    default String lastMkt() {
        throw new UnsupportedOperationException();
    }
}
