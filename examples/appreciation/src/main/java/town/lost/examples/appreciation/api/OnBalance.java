package town.lost.examples.appreciation.api;

import im.xcl.platform.dto.VanillaSignedMessage;
import net.openhft.chronicle.wire.HexadecimalLongConverter;
import net.openhft.chronicle.wire.LongConversion;

public class OnBalance extends VanillaSignedMessage<OnBalance> {
    @LongConversion(HexadecimalLongConverter.class)
    private long account;
    private double amount;

    public OnBalance() {
        super(2, 2);
    }

    public OnBalance init(long account, double amount) {
        this.account = account;
        this.amount = amount;
        return this;
    }

    public long account() {
        return account;
    }

    public OnBalance account(long account) {
        this.account = account;
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
