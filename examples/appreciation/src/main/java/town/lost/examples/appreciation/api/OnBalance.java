package town.lost.examples.appreciation.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class OnBalance extends AbstractBytesMarshallable {
    private BytesStore publicKey;
    private double amount;

    public OnBalance() {
    }

    public OnBalance(BytesStore publicKey, double amount) {
        init(publicKey, amount);
    }

    public OnBalance init(BytesStore publicKey, double amount) {
        this.publicKey = publicKey;
        this.amount = amount;
        return this;
    }

    public BytesStore publicKey() {
        return publicKey;
    }

    public OnBalance publicKey(BytesStore publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public double amount() {
        return amount;
    }

    public OnBalance amount(double amount) {
        this.amount = amount;
        return this;
    }
}
