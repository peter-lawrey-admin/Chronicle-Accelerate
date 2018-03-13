package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface TradeDate {
    /**
     * Tag number for this field
     */
    int FIELD = 75;

    /**
     * @param tradeDate &gt; FIX TAG 75
     */
    void tradeDate(String tradeDate);

    default String tradeDate() {
        throw new UnsupportedOperationException();
    }
}
