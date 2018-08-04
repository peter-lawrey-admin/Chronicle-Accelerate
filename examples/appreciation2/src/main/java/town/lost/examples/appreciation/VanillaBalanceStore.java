package town.lost.examples.appreciation;

import cash.xcl.util.XCLLongDoubleMap;
import net.openhft.chronicle.bytes.BytesStore;

public class VanillaBalanceStore implements BalanceStore {
    private final XCLLongDoubleMap amountsMap = XCLLongDoubleMap.withExpectedSize(1024);

    private static long getKey(BytesStore bytesStore) {
        return bytesStore.readLong(bytesStore.readRemaining() - Long.BYTES);
    }

    @Override
    public double getBalance(BytesStore bytesStore) {
        long key = getKey(bytesStore);
        double amount;
        synchronized (amountsMap) {
            amount = amountsMap.getOrDefault(key, Long.MIN_VALUE);
        }
        return amount == Long.MIN_VALUE ? Double.NaN : amount;
    }

    @Override
    public boolean subtractBalance(BytesStore bytesStore, double amount) {
        assert amount >= 0;
        long key = getKey(bytesStore);
        synchronized (amountsMap) {
            double amount2 = amountsMap.getOrDefault(key, 0);
            amount2 -= amount;
            if (amount2 < 0)
                return false;
            amountsMap.justPut(key, amount2);
            return true;
        }
    }

    @Override
    public void addBalance(BytesStore bytesStore, double amount) {
        assert amount >= 0;
        long key = getKey(bytesStore);
        synchronized (amountsMap) {
            double amount2 = amountsMap.getOrDefault(key, 0);
            amount2 += amount;
            amountsMap.justPut(key, amount2);
        }
    }

    @Override
    public void setBalance(BytesStore bytesStore, double amount) {
        assert amount >= 0;
        long key = getKey(bytesStore);
        synchronized (amountsMap) {
            amountsMap.justPut(key, amount);
        }
    }
}
