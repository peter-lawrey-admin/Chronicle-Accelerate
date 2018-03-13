package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface AvgPx {
    /**
     * Tag number for this field
     */
    int FIELD = 6;

    /**
     * @param avgPx &gt; FIX TAG 6
     */
    void avgPx(double avgPx, int avgPx_dp);

    default double avgPx() {
        throw new UnsupportedOperationException();
    }

    default int avgPx_dp() {
        return FixMessage.UNSET_DP;
    }
}
