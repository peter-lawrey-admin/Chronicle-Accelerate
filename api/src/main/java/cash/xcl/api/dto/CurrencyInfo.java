package cash.xcl.api.dto;

import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class CurrencyInfo extends AbstractBytesMarshallable {
    private final String currency;
    private final String description;

    public CurrencyInfo(String currency, String description) {
        this.currency = currency;
        this.description = description;
    }

    public String currency() {
        return currency;
    }

    public String description() {
        return description;
    }
}
