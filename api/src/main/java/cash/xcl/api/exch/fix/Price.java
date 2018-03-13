package cash.xcl.api.exch.fix;



/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface Price {
    /**
     * Tag number for this field
     */
    int FIELD = 44;

    /**
     * @param price &gt; FIX TAG 44
     */
    void price(double price, int price_dp);

    default double price() {
        throw new UnsupportedOperationException();
    }

    default int price_dp() {
        return FixMessage.UNSET_DP;
    }
}
