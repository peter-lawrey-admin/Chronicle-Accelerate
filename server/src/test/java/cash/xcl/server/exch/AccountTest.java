package cash.xcl.server.exch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccountTest {
    private static final double EPSILON = Math.pow(10, -10);

    @Test
    public void basicFlow() throws TransactionFailedException {
        Account account = new Account(12345, 10);
        assertEquals(12345, account.getAccountId());
        assertEquals(10.0, account.money(), EPSILON);
        assertEquals(10.0, account.availableMoney(), EPSILON);
        assertEquals(0, account.lockedMoney(), EPSILON);
        assertEquals(25.0, account.deposit(15), EPSILON);
        assertEquals(25.0, account.money(), EPSILON);
        assertEquals(25.0, account.availableMoney(), EPSILON);
        assertEquals(0, account.lockedMoney(), EPSILON);
        assertTrue(account.lockMoney(15));
        assertEquals(25.0, account.money(), EPSILON);
        assertEquals(10.0, account.availableMoney(), EPSILON);
        assertEquals(15, account.lockedMoney(), EPSILON);
        assertFalse(account.lockMoney(15));
        assertTrue(account.lockMoney(10));
        assertTrue(account.unlockMoney(10));
        assertTrue(account.unlockMoney(10));
        assertEquals(25.0, account.money(), EPSILON);
        assertEquals(20.0, account.availableMoney(), EPSILON);
        assertEquals(5, account.lockedMoney(), EPSILON);
        assertFalse(account.unlockMoney(10));
        assertEquals(15.0, account.withdraw(10), EPSILON);
        assertEquals(15.0, account.money(), EPSILON);
        assertEquals(10.0, account.availableMoney(), EPSILON);
        assertEquals(5, account.lockedMoney(), EPSILON);
        account.unlockMoney(5);
        assertEquals(15.0, account.money(), EPSILON);
        assertEquals(15.0, account.availableMoney(), EPSILON);
        assertEquals(0, account.lockedMoney(), EPSILON);
        account.withdraw(10);
        assertEquals(5.0, account.money(), EPSILON);
        account.withdraw(5);
        assertEquals(0.0, account.money(), EPSILON);

    }

    @Test(expected = TransactionFailedException.class)
    public void unsufficientFunds1() throws TransactionFailedException {
        Account account = new Account(12345);
        account.withdraw(1234);
    }

    @Test(expected = TransactionFailedException.class)
    public void unsufficientFunds2() throws TransactionFailedException {
        Account account = new Account(12345, 50);
        account.withdraw(50.1);
    }

}
