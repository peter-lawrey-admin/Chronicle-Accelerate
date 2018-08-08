package town.lost.examples.appreciation;

public interface BalanceStore {
    double getBalance(long account);

    boolean subtractBalance(long account, double amount);

    void addBalance(long account, double amount);

    void setBalance(long account, double amount);
}
