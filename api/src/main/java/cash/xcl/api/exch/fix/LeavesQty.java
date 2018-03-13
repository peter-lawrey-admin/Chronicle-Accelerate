package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface LeavesQty {
    /**
     * Tag number for this field
     */
    int FIELD = 151;

    /**
     * @param leavesQty &gt; FIX TAG 151
     */
    void leavesQty(double leavesQty, int leavesQty_dp);

    default double leavesQty() {
        throw new UnsupportedOperationException();
    }

    default int leavesQty_dp() {
        return FixMessage.UNSET_DP;
    }
}
