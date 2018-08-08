package town.lost.examples.appreciation.api;

import im.xcl.platform.dto.VanillaSignedMessage;
import net.openhft.chronicle.wire.HexadecimalLongConverter;
import net.openhft.chronicle.wire.LongConversion;

public class QueryBalance extends VanillaSignedMessage<QueryBalance> {
    @LongConversion(HexadecimalLongConverter.class)
    private long account;

    public QueryBalance() {
        super(2, 4);
    }

    public long account() {
        return account;
    }

    public QueryBalance account(long account) {
        this.account = account;
        return this;
    }
}
