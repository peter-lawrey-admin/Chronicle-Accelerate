package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface LastPx {
    /**
     * Tag number for this field
     */
    int FIELD = 31;

    /**
     * @param lastPx &gt; FIX TAG 31
     */
    void lastPx(double lastPx, int lastPx_dp);

    default double lastPx() {
        throw new UnsupportedOperationException();
    }

    default int lastPx_dp() {
        return FixMessage.UNSET_DP;
    }
}
