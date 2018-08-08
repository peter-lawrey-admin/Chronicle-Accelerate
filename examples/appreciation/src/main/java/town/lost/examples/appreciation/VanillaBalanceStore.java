package town.lost.examples.appreciation;

import cash.xcl.util.XCLLongDoubleMap;
import net.openhft.chronicle.bytes.BytesStore;

public class VanillaBalanceStore implements BalanceStore {
    private final XCLLongDoubleMap amountsMap = XCLLongDoubleMap.withExpectedSize(1024);

    private static long getKey(BytesStore bytesStore) {
        return bytesStore.readLong(bytesStore.readRemaining() - Long.BYTES);
    }

    @Override
    public double getBalance(long account) {
        double amount;
        synchronized (amountsMap) {
            amount = amountsMap.getOrDefault(account, Long.MIN_VALUE);
        }
        return amount == Long.MIN_VALUE ? Double.NaN : amount;
    }

    @Override
    public boolean subtractBalance(long account, double amount) {
        assert amount >= 0;
        synchronized (amountsMap) {
            double amount2 = amountsMap.getOrDefault(account, 0);
            amount2 -= amount;
            if (amount2 < 0)
                return false;
            amountsMap.justPut(account, amount2);
            return true;
        }
    }

    @Override
    public void addBalance(long account, double amount) {
        assert amount >= 0;
        synchronized (amountsMap) {
            double amount2 = amountsMap.getOrDefault(account, 0);
            amount2 += amount;
            amountsMap.justPut(account, amount2);
        }
    }

    @Override
    public void setBalance(long account, double amount) {
        assert amount >= 0;
        synchronized (amountsMap) {
            amountsMap.justPut(account, amount);
        }
    }
}
