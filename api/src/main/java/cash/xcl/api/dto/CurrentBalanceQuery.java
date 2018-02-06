package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

// FIXME needs reviewing/completing
public class CurrentBalanceQuery extends SignedMessage {

    private long address;

    public CurrentBalanceQuery(long sourceAddress, long eventTime, long address) {
        super(sourceAddress, eventTime);
        this.address = address;
    }

    public CurrentBalanceQuery() {

    }

    public long address() {
        return address;
    }

    public CurrentBalanceQuery address(long address) {
        this.address = address;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {

    }

    @Override
    public int messageType() {
        return MethodIds.CURRENT_BALANCE_QUERY;
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {

    }
}
