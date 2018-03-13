package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface IDSource {
    /**
     * Tag number for this field
     */
    int FIELD = 22;

    String CUSIP = "1";

    String SEDOL = "2";

    String QUIK = "3";

    String ISIN_NUMBER = "4";

    String RIC_CODE = "5";

    String ISO_CURRENCY_CODE = "6";

    String ISO_COUNTRY_CODE = "7";

    String EXCHANGE_SYMBOL = "8";

    String CONSOLIDATED_TAPE_ASSOCIATION = "9";

    static String asString(String value) {
        return value;
    }

    /**
     * @param idSource &gt; FIX TAG 22
     */
    void idSource(String idSource);

    default String idSource() {
        throw new UnsupportedOperationException();
    }
}
