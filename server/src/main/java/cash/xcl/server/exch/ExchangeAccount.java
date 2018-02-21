package cash.xcl.server.exch;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


class ExchangeAccount {
    private final String currency;

    private final AtomicReference<Double> totalValue = new AtomicReference<>(0D);
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
        totalValue.accumulateAndGet(money, (a, b) -> a + b);
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
        totalValue.accumulateAndGet(money, (a, b) -> a - b);
    }

    double getValue(long accountAddress) {
        Account account = accounts.get(accountAddress);
        if (account == null) {
            return 0.0;
        } else {
            return account.getValue();
        }
    }

    double getTotalValue() {
        return totalValue.get().doubleValue();
    }

    double computeTotalValue() {
        throw new UnsupportedOperationException();
    }

    public String getCurrency() {
        return currency;
    }

}
