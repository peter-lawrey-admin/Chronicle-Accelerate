package cash.xcl.api.dto;

import cash.xcl.api.util.XCLBase32;
import cash.xcl.api.util.XCLBase32IntConverter;
import cash.xcl.util.XCLIntDoubleMap;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static cash.xcl.api.dto.Validators.validNumber;

public class CurrentBalanceResponse2 extends SignedMessage {
    private long address;

    private XCLIntDoubleMap balances = XCLIntDoubleMap.withExpectedSize(16);


    public CurrentBalanceResponse2(long sourceAddress, long eventTime, long address, Map<String, Double> balances) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
    }

    public CurrentBalanceResponse2(long sourceAddress, long eventTime, long address, XCLIntDoubleMap balances) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
    }

    public CurrentBalanceResponse2 init(long sourceAddress, long eventTime, long address, XCLIntDoubleMap balances) {
        super.init(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
        return this;
    }

    public CurrentBalanceResponse2 init(long sourceAddress, long eventTime, long address, Map<String, Double> balances) {
        super.init(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
        return this;
    }


    public CurrentBalanceResponse2() {

    }

    public CurrentBalanceResponse2(long address) {
        address(address);
    }

    @Override
    public final void readMarshallable2(@SuppressWarnings("rawtypes") BytesIn bytes) throws IORuntimeException {
        address(bytes.readStopBit());
        int balancesCount = bytes.readInt();
        if (balances == null) {
            balances = XCLIntDoubleMap.withExpectedSize(16);
        } else {
            balances.clear();
        }
        for (int i = 0; i < balancesCount; i++) {
            int symbol = bytes.readInt();
            double amount = bytes.readStopBitDouble();
            balance(symbol, amount);
        }
    }


    void balance(int symbol, double amount) {
        balances.put(symbol, validNumber(amount));
    }

    @Override
    public final void writeMarshallable2(@SuppressWarnings("rawtypes") BytesOut bytes) {
        bytes.writeStopBit(address);
        if (balances != null) {
            bytes.writeInt(balances.size());
            balances.forEach((k, v) -> {
                bytes.writeInt(k);
                bytes.writeStopBit(v);
            });
        } else {
            bytes.writeInt(0);
        }
    }

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        sourceAddress(wire.read("sourceAddress").int64());
        eventTime(wire.read("eventTime").int64());
        address(wire.read("address").int64());
        if (balances == null) balances = XCLIntDoubleMap.withExpectedSize(8);
        wire.read("balances").marshallable(m -> {
            while (m.hasMore()) {
                int currencyId = XCLBase32IntConverter.INSTANCE.parse(m.readEvent(String.class));
                balances.put(currencyId, m.getValueIn().float64());
            }
        });
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        wire.write("sourceAddress").int64(sourceAddress());
        wire.write("eventTime").int64(eventTime());
        wire.write("address").int64(address());
        wire.write("balance").marshallable(m -> {
            balances.forEach((c, v) -> {
                wire.write(XCLBase32IntConverter.asString(c)).float64(v);
            });
        });
    }

    public double balance(int symbol) {
        return balances.get(symbol);
    }

    public long address() {
        return address;
    }

    private void address(long address) {
        this.address = address;
    }

    public CurrentBalanceResponse2 balances(Map<String, Double> newBalances) {
        newBalances.forEach((k, v) -> balances.put(XCLBase32.decodeInt(k), v));
        return this;
    }

    public CurrentBalanceResponse2 balances(XCLIntDoubleMap newBalances) {
        this.balances.putAll(newBalances);
        return this;
    }

    public void print() {
        balances.forEach((k, v) -> System.out.print(" " + XCLBase32.encodeIntUpper(k) + " = " + v));
    }

    @Override
    public int messageType() {
        return MessageTypes.CURRENT_BALANCE_RESPONSE;
    }


}
