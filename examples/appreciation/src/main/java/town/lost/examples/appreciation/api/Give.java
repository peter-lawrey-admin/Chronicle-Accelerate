package town.lost.examples.appreciation.api;

import im.xcl.platform.dto.SelfSignedMessage;
import net.openhft.chronicle.wire.HexadecimalLongConverter;
import net.openhft.chronicle.wire.LongConversion;

public class Give extends SelfSignedMessage<Give> {
    @LongConversion(HexadecimalLongConverter.class)
    long receiverAddress;
    double amount;

    public Give() {
        super(2, 1);
    }

    public Give init(long receiverAddress, double amount) {
        this.receiverAddress = receiverAddress;
        this.amount = amount;
        return this;
    }

    public long receiverAddress() {
        return receiverAddress;
    }

    public Give receiverAddress(long receiverAddress) {
        this.receiverAddress = receiverAddress;
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
