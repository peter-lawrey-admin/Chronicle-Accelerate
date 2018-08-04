package town.lost.examples.appreciation.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class OpeningBalance extends AbstractBytesMarshallable {
    private BytesStore publicKey;
    private double amount;

    public OpeningBalance() {
    }

    public OpeningBalance(BytesStore publicKey, double amount) {
        init(publicKey, amount);
    }

    public OpeningBalance init(BytesStore publicKey, double amount) {
        this.publicKey = publicKey;
        this.amount = amount;
        return this;
    }

    public BytesStore publicKey() {
        return publicKey;
    }

    public OpeningBalance publicKey(BytesStore publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public double amount() {
        return amount;
    }

    public OpeningBalance amount(double amount) {
        this.amount = amount;
        return this;
    }
}
