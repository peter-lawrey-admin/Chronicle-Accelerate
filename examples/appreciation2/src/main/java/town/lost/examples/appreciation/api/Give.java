package town.lost.examples.appreciation.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class Give extends AbstractBytesMarshallable {
    BytesStore publicKey;
    double amount;

    public Give() {
    }

    public Give(BytesStore publicKey, double amount) {
        init(publicKey, amount);
    }

    public void init(BytesStore publicKey, double amount) {
        this.publicKey = publicKey;
        this.amount = amount;
    }

    public BytesStore publicKey() {
        return publicKey;
    }

    public Give publicKey(BytesStore publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public double amount() {
        return amount;
    }

    public Give amount(double amount) {
        this.amount = amount;
        return this;
    }
}
