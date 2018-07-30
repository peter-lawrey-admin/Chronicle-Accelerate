package town.lost.examples.appreciation;

import net.openhft.chronicle.bytes.BytesStore;

public interface BalanceStore {
    double getBalance(BytesStore bytesStore);

    boolean subtractBalance(BytesStore bytesStore, double amount);

    void addBalance(BytesStore bytesStore, double amount);

    void setBalance(BytesStore bytesStore, double amount);
}
