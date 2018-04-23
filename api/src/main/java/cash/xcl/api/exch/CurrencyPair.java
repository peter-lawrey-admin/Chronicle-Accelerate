package cash.xcl.api.exch;

import cash.xcl.util.Validators;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class CurrencyPair extends AbstractBytesMarshallable {

    private String base;
    private String quote;

    public CurrencyPair() {

    }

    public CurrencyPair(String first, String second) {
        setPair(first, second);
    }

    @Override
    public void readMarshallable(@SuppressWarnings("rawtypes") BytesIn bytes) throws IORuntimeException {
        if (bytes.readRemaining() > 0)
            setPair(bytes.readUtf8(), bytes.readUtf8());
        else
            base = quote = "";
    }

    @Override
    public void writeMarshallable(@SuppressWarnings("rawtypes") BytesOut bytes) {
        bytes.writeUtf8(base);
        bytes.writeUtf8(quote);
    }

    private void setPair(String first, String second) {
        this.base = Validators.notNullOrEmpty(first).trim();
        this.quote = Validators.notNullOrEmpty(second).trim();
        if (first.equalsIgnoreCase(second)) {
            throw new IllegalArgumentException();
        }
    }

    public String getBaseCurrency() {
        return base;
    }

    public String getQuoteCurrency() {
        return quote;
    }

}
