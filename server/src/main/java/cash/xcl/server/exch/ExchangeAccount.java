package cash.xcl.server.exch;

import java.util.HashMap;

import net.openhft.chronicle.core.annotation.SingleThreaded;

@SingleThreaded
class ExchangeAccount {
    private final String currency;

    private double totalValue = 0;
    private final HashMap<Long, Account> accounts = new HashMap<>();

    ExchangeAccount(String currency) {
        this.currency = currency;
    }

    /**
     * @throws TransactionFailedException
     */
    void deposit(long accountAddress, double money) throws TransactionFailedException {
        Account account = accounts.putIfAbsent(accountAddress, new Account(accountAddress, money));
        if (account != null) {
            account.deposit(money);
        }
        totalValue += money;
    }

    /**
     * @throws TransactionFailedException
     */
    void withdraw(long accountAddress, double money) throws TransactionFailedException {
        Account account = accounts.get(accountAddress);
        if (account == null) {
            throw new IllegalArgumentException("Unknown Address");
        }
        account.withdraw(money);
        totalValue -= money;
    }

    double getValue(long accountAddress) {
        Account account = accounts.get(accountAddress);
        if (account == null) {
            return 0.0;
        } else {
            return account.money();
        }
    }

    double getTotalValue() {
        return totalValue;
    }

    double computeTotalValue() {
        return accounts.values().stream().map((a) -> a.money()).reduce(0D, (a, b) -> a + b);
    }

    public String getCurrency() {
        return currency;
    }

}
