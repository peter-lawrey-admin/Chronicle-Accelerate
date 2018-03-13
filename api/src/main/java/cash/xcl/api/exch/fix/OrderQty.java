package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface OrderQty {
    /**
     * Tag number for this field
     */
    int FIELD = 38;

    /**
     * @param orderQty &gt; FIX TAG 38
     */
    void orderQty(double orderQty, int orderQty_dp);

    default double orderQty() {
        throw new UnsupportedOperationException();
    }

    default int orderQty_dp() {
        return FixMessage.UNSET_DP;
    }
}
