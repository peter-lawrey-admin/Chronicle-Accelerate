package cash.xcl.api.dto;

import cash.xcl.util.XCLBase32;
import cash.xcl.util.XCLBase32LongConverter;
import cash.xcl.util.XCLIntDoubleMap;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.LongConversion;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static cash.xcl.util.Validators.validNumber;

public class CurrentBalanceResponse extends SignedBinaryMessage {
    @LongConversion(XCLBase32LongConverter.class)
    private long address;

    private XCLIntDoubleMap balances = XCLIntDoubleMap.withExpectedSize(16);


    public CurrentBalanceResponse(long sourceAddress, long eventTime, long address, Map<String, Double> balances) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
    }

    public CurrentBalanceResponse(long sourceAddress, long eventTime, long address, XCLIntDoubleMap balances) {
        super(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
    }

    public CurrentBalanceResponse init(long sourceAddress, long eventTime, long address, XCLIntDoubleMap balances) {
        super.init(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
        return this;
    }

    public CurrentBalanceResponse init(long sourceAddress, long eventTime, long address, Map<String, Double> balances) {
        super.init(sourceAddress, eventTime);
        this.address = address;
        this.balances(balances);
        return this;
    }


    public CurrentBalanceResponse() {

    }

    public CurrentBalanceResponse(long address) {
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
        sourceAddress(XCLBase32.decode(wire.read("sourceAddress").text()));
        eventTime(wire.read("eventTime").int64());
        address(XCLBase32.decode(wire.read("address").text()));
        if (balances == null) balances = XCLIntDoubleMap.withExpectedSize(8);
        wire.read("balances").marshallable(m -> {
            while (m.hasMore()) {
                int currencyId = XCLBase32.decodeInt(m.readEvent(String.class));
                balances.put(currencyId, m.getValueIn().float64());
            }
        });
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        wire.write("sourceAddress").text(XCLBase32.encode(sourceAddress()));
        wire.write("eventTime").int64(eventTime());
        wire.write("address").text(XCLBase32.encode(address()));
        wire.write("balances").marshallable(m -> {
            balances.forEach((c, v) -> {
                wire.write(XCLBase32.encodeIntUpper(c)).float64(v);
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

    public CurrentBalanceResponse balances(Map<String, Double> newBalances) {
        newBalances.forEach((k, v) -> balances.put(XCLBase32.decodeInt(k), v));
        return this;
    }

    public CurrentBalanceResponse balances(XCLIntDoubleMap newBalances) {
        this.balances.putAll(newBalances);
        return this;
    }

    public void print() {
        balances.forEach((k, v) -> System.out.print(" " + XCLBase32.encodeIntUpper(k) + " = " + v));
    }

    @Override
    public int intMessageType() {
        return MessageTypes.CURRENT_BALANCE_RESPONSE;
    }


}
