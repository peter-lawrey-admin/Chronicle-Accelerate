package cash.xcl.server.exch;

import static cash.xcl.api.dto.Validators.notInfinite;
import static cash.xcl.api.dto.Validators.positive;

public class Account {

    private final long accountId;

    private double money;

    private double lockedMoney;


    public Account(long accountId) {
        this(accountId, 0D);
    }

    public Account(long accountAddress, double money) {
        this.accountId = accountAddress;
        this.money = notInfinite(positive(money));
    }

    public double deposit(double deposit) {
        double depositValue = notInfinite(positive(deposit));
        money += depositValue;
        return money;
    }


    public double withdraw(double withdraw) throws TransactionFailedException {
        double withdrawValue = notInfinite(positive(withdraw));
        if (withdrawValue < availableMoney()) {
            money -= withdrawValue;
            return money;
        } else {
            throw new TransactionFailedException("Unsufficient funds");
        }
    }

    boolean lockMoney(double amount) {
        if (availableMoney() <= amount) {
            lockedMoney += amount;
            return true;
        }
        return false;
    }

    boolean unlockMoney(double amount) {
        if (lockedMoney <= amount) {
            lockedMoney -= amount;
            return true;
        }
        return false;
    }

    double availableMoney() {
        return money - lockedMoney;
    }

    public void transferTo(Account account, double transfer) throws TransactionFailedException {
        try {
            this.withdraw(transfer);
            account.deposit(transfer);
        } catch (Exception ex) {
            throw new TransactionFailedException(ex);
        }
    }

    public double getValue() {
        return money;
    }

    public long getAccountId() {
        return accountId;
    }


}
