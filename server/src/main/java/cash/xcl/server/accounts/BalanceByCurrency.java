package cash.xcl.server.accounts;


import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static cash.xcl.api.dto.Validators.notNullOrEmpty;
import static cash.xcl.api.dto.Validators.validNumber;


public class BalanceByCurrency extends AbstractBytesMarshallable {
    private static final Double ZERO = 0D;

    private long address;

    private Map<String, Double> balances = new HashMap<>();

    public BalanceByCurrency(long address) {
        setAddress(address);
    }

    @Override
    public final void readMarshallable(@SuppressWarnings("rawtypes") BytesIn bytes) throws IORuntimeException {
        setAddress(bytes.readStopBit());
        int balancesCount = bytes.readInt();
        if (balances == null) {
            balances = new HashMap<>();
        } else {
            balances.clear();
        }
        for (int i = 0; i < balancesCount; i++) {
            String symbol = bytes.readUtf8();
            double amount = bytes.readStopBitDouble();
            setBalance(symbol, amount);
        }

    }

    // todo: make private
    public void setBalance(String symbol, double amount) {

        balances.put(notNullOrEmpty(symbol), validNumber(amount));
    }

    @Override
    public final void writeMarshallable(@SuppressWarnings("rawtypes") BytesOut bytes) {
        bytes.writeStopBit(address);
        if (balances != null) {
            bytes.writeInt(balances.size());
            balances.forEach((k, v) -> {
                bytes.writeUtf8(k);
                bytes.writeStopBit(v);
            });
        } else {
            bytes.writeInt(0);
        }
    }

    public double getBalance(String symbol) {
        Double balance = balances.getOrDefault(symbol, ZERO);
        return balance.doubleValue();
    }

    public Iterable<String> getCurrencies() {
        return Collections.unmodifiableSet(balances.keySet());
    }

    public long getAddress() {
        return address;
    }

    private void setAddress(long address) {
        this.address = address;
    }

    public BalanceByCurrency setBalances(Map<String, Double> newBalances) {
        if( !this.balances.isEmpty() ) {
            throw new IllegalArgumentException("opening balances can only be set once");
        }
        this.balances.putAll(newBalances);
        return this;
    }

    public void print() {
        //System.out.println( "    " + address);
        for (Map.Entry entry : balances.entrySet()) {
            String symbol = (String) entry.getKey();
            Double balance = (Double) entry.getValue();
            System.out.println( "    " + symbol + " = " + balance );
        }
    }

}
