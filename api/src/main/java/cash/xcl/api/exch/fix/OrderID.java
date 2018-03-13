package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface OrderID {
    /**
     * Tag number for this field
     */
    int FIELD = 37;

    /**
     * @param orderID &gt; FIX TAG 37
     */
    void orderID(String orderID);

    default String orderID() {
        throw new UnsupportedOperationException();
    }
}
