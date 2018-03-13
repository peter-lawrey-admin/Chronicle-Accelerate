package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface LastShares {
    /**
     * Tag number for this field
     */
    int FIELD = 32;

    /**
     * @param lastShares &gt; FIX TAG 32
     */
    void lastShares(double lastShares, int lastShares_dp);

    default double lastShares() {
        throw new UnsupportedOperationException();
    }

    default int lastShares_dp() {
        return FixMessage.UNSET_DP;
    }
}
