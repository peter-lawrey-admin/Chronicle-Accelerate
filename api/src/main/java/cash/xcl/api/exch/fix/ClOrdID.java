package cash.xcl.api.exch.fix;

import net.openhft.chronicle.bytes.Bytes;

/**
 * Generated at software.chronicle.fix.codegen.FieldGenerator.generateField(FieldGenerator.java:44)
 */
public interface ClOrdID {
    /**
     * Tag number for this field
     */
    int FIELD = 11;

    /**
     * @param clOrdID &gt; FIX TAG 11
     */
    void clOrdID(Bytes clOrdID);

    default Bytes clOrdID() {
        throw new UnsupportedOperationException();
    }

    default Bytes clOrdID_buffer() {
        return null;
    }
}
