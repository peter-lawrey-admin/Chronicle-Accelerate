package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java)
 */
public interface MaturityMonthYear {
    /**
     * Tag number for this field
     */
    int FIELD = 200;

    /**
     * @param maturityMonthYear &gt; FIX TAG 200
     */
    void maturityMonthYear(String maturityMonthYear);

    default String maturityMonthYear() {
        throw new UnsupportedOperationException();
    }
}
