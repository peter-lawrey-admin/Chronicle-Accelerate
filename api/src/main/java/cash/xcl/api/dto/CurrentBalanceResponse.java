package cash.xcl.api.dto;


import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

import java.util.Map;

// FIXME needs reviewing/completing
public class CurrentBalanceResponse extends CurrentBalanceEvent {

    private CurrentBalanceQuery currentBalanceQuery;

    // TODO: use this instead of inheritance
    //private CurrentBalanceEvent currentBalanceEvent;


    public CurrentBalanceResponse(long sourceAddress, long eventTime,
                                  long address,
                                  Map<String, Double> balances,
                                  CurrentBalanceQuery currentBalanceQuery) {
        super(sourceAddress, eventTime, address, balances);
        this.currentBalanceQuery = currentBalanceQuery;
        assert this.address == currentBalanceQuery.address();
    }

    public CurrentBalanceResponse(CurrentBalanceQuery currentBalanceQuery) {
        this.currentBalanceQuery = currentBalanceQuery;
    }

    public CurrentBalanceResponse() {
    }


    public CurrentBalanceQuery currentBalanceQuery() {
        return currentBalanceQuery;
    }

    public CurrentBalanceResponse currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        this.currentBalanceQuery = currentBalanceQuery;
        return this;
    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
    }

    @Override
    public int messageType() {
        return MethodIds.CURRENT_BALANCE_RESPONSE;
    }
}
