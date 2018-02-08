package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurrentBalanceEvent extends SignedMessage {
    long address;
    Map<String, Double> balances = new LinkedHashMap<>();

    public CurrentBalanceEvent(long sourceAddress, long eventTime, long address, Map<String, Double> balances) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.balances = balances;
    }

    public CurrentBalanceEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        address = bytes.readLong();
        int count = (int) bytes.readStopBit();
        if (balances == null) balances = new LinkedHashMap<>();
        for (int i = 0; i < count; i++)
            balances.put(bytes.readUtf8(), bytes.readDouble());
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        bytes.writeLong(address);
        bytes.writeStopBit(balances.size());
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            bytes.writeUtf8(entry.getKey());
            bytes.writeDouble(entry.getValue());
        }
    }

    @Override
    public int messageType() {
        return MessageTypes.CURRENT_BALANCE_EVENT;
    }

    public long address() {
        return address;
    }

    public CurrentBalanceEvent address(long address) {
        this.address = address;
        return this;
    }

    public Map<String, Double> balances() {
        return balances;
    }

    public CurrentBalanceEvent balances(Map<String, Double> balances) {
        this.balances = balances;
        return this;
    }
}
