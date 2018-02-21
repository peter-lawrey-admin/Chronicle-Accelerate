package cash.xcl.server.exch;

import static cash.xcl.api.dto.Validators.notInfinite;
import static cash.xcl.api.dto.Validators.positive;

import net.openhft.chronicle.core.annotation.SingleThreaded;

@SingleThreaded
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
        if (withdrawValue <= availableMoney()) {
            money -= withdrawValue;
            return money;
        } else {
            throw new TransactionFailedException("Unsufficient funds");
        }
    }

    boolean lockMoney(double amount) {
        if (availableMoney() >= amount) {
            lockedMoney += amount;
            return true;
        }
        return false;
    }

    boolean unlockMoney(double amount) {
        if (lockedMoney >= amount) {
            lockedMoney -= amount;
            return true;
        }
        return false;
    }

    double availableMoney() {
        return money - lockedMoney;
    }

    double lockedMoney() {
        return lockedMoney;
    }

    double money() {
        return money;
    }

    long getAccountId() {
        return accountId;
    }
}
