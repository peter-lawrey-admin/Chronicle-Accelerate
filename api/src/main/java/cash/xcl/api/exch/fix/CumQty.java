package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface CumQty {
    /**
     * Tag number for this field
     */
    int FIELD = 14;

    /**
     * @param cumQty &gt; FIX TAG 14
     */
    void cumQty(double cumQty, int cumQty_dp);

    default double cumQty() {
        throw new UnsupportedOperationException();
    }

    default int cumQty_dp() {
        return FixMessage.UNSET_DP;
    }
}
