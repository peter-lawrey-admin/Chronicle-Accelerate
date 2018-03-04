package cash.xcl.server.accounts;


import cash.xcl.api.util.XCLBase32;
import cash.xcl.util.XCLIntDoubleMap;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

import java.util.Map;

import static cash.xcl.api.dto.Validators.validNumber;


public class BalanceByCurrency extends AbstractBytesMarshallable {
    private long address;

    private XCLIntDoubleMap balances = XCLIntDoubleMap.withExpectedSize(16);

    public BalanceByCurrency(long address) {
        setAddress(address);
    }

    @Override
    public final void readMarshallable(@SuppressWarnings("rawtypes") BytesIn bytes) throws IORuntimeException {
        setAddress(bytes.readStopBit());
        int balancesCount = bytes.readInt();
        if (balances == null) {
            balances = XCLIntDoubleMap.withExpectedSize(16);
        } else {
            balances.clear();
        }
        for (int i = 0; i < balancesCount; i++) {
            int symbol = bytes.readInt();
            double amount = bytes.readStopBitDouble();
            setBalance(symbol, amount);
        }

    }

    void setBalance(int symbol, double amount) {
        balances.put(symbol, validNumber(amount));
    }

    @Override
    public final void writeMarshallable(@SuppressWarnings("rawtypes") BytesOut bytes) {
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

    public double getBalance(int symbol) {
        return balances.get(symbol);
    }

    public long getAddress() {
        return address;
    }

    private void setAddress(long address) {
        this.address = address;
    }

    public BalanceByCurrency setBalances(Map<String, Double> newBalances) {
        if (!this.balances.isEmpty()) {
            throw new IllegalArgumentException("opening balances can only be set once");
        }
        newBalances.forEach((k, v) -> balances.put(XCLBase32.decodeInt(k), v));
        return this;
    }

    public BalanceByCurrency setBalances(XCLIntDoubleMap newBalances) {
        if (!this.balances.isEmpty()) {
            throw new IllegalArgumentException("opening balances can only be set once");
        }
        this.balances.putAll(newBalances);
        return this;
    }

    public void print() {
        balances.forEach((k, v) -> System.out.print(" " + XCLBase32.encodeIntUpper(k) + " = " + v));
    }

}
