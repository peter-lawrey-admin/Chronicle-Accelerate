package cash.xcl.api.dto;

import cash.xcl.util.XCLBase32LongConverter;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.wire.LongConversion;

public class CurrentBalanceQuery extends SignedBinaryMessage {

    @LongConversion(XCLBase32LongConverter.class)
    private long address;

    public CurrentBalanceQuery(long sourceAddress, long eventTime, long address) {
        super(sourceAddress, eventTime);
        this.address = address;
    }

    public CurrentBalanceQuery init(long sourceAddress, long eventTime, long address) {
        super.init(sourceAddress, eventTime);
        this.address = address;
        return this;
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
    protected void readMarshallable2(BytesIn<?> bytes) {
        this.address = bytes.readLong();
    }

    @Override
    public int intMessageType() {
        return MessageTypes.CURRENT_BALANCE_QUERY;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(address);
    }
}
