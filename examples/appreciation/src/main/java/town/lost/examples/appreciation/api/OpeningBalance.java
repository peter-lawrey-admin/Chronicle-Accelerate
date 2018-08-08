package town.lost.examples.appreciation.api;

import im.xcl.platform.dto.VanillaSignedMessage;
import net.openhft.chronicle.bytes.BytesStore;

public class OpeningBalance extends VanillaSignedMessage<OpeningBalance> {
    private BytesStore account;
    private double amount;

    public OpeningBalance() {
        super(2, 5);
    }

    public OpeningBalance(BytesStore account, double amount) {
        super(2, 5);
        init(account, amount);
    }

    public OpeningBalance init(BytesStore account, double amount) {
        this.account = account;
        this.amount = amount;
        return this;
    }

    public BytesStore account() {
        return account;
    }

    public OpeningBalance account(BytesStore account) {
        this.account = account;
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
