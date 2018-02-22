package cash.xcl.server.exch;

import static cash.xcl.api.dto.Validators.notInfinite;
import static cash.xcl.api.dto.Validators.positive;

import net.openhft.chronicle.core.annotation.SingleThreaded;

@SingleThreaded
final class Account {

    private double money;
    private double lockedMoney;

    Account() {
        this(0D);
    }

    Account(double money) {
        this.money = notInfinite(positive(money));
    }

    double deposit(double deposit) {
        double depositValue = notInfinite(positive(deposit));
        money += depositValue;
        return money;
    }


    double withdraw(double withdraw) throws TransactionFailedException {
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


}
