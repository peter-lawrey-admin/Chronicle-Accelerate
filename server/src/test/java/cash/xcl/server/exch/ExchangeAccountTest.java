package cash.xcl.server.exch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cash.xcl.server.exch.ExchangeAccount;
import cash.xcl.server.exch.TransactionFailedException;

public class ExchangeAccountTest {

    private static final double EPSILON = Math.pow(10, -10);

    @Test
    public void testDeposit() throws TransactionFailedException {
        ExchangeAccount exchAccount = new ExchangeAccount("EUR");
        long accountAddress1 = 1234567L;
        exchAccount.deposit(accountAddress1, 50);
        assertEquals(50, exchAccount.getValue(accountAddress1), EPSILON);
        exchAccount.deposit(accountAddress1, 150);
        assertEquals(200, exchAccount.getValue(accountAddress1), EPSILON);
        assertEquals(200, exchAccount.getTotalValue(), EPSILON);
        assertEquals(exchAccount.getTotalValue(), exchAccount.computeTotalValue(), EPSILON);

        long accountAddress2 = 7654321L;
        exchAccount.deposit(accountAddress2, 100);
        assertEquals(100, exchAccount.getValue(accountAddress2), EPSILON);
        assertEquals(300, exchAccount.getTotalValue(), EPSILON);
        assertEquals(exchAccount.getTotalValue(), exchAccount.computeTotalValue(), EPSILON);

    }

    @Test
    public void testWithdraw() throws TransactionFailedException {
        ExchangeAccount exchAccount = new ExchangeAccount("EUR");
        long accountAddress1 = 1234567L;
        exchAccount.deposit(accountAddress1, 50);
        assertEquals(50, exchAccount.getValue(accountAddress1), EPSILON);
        exchAccount.deposit(accountAddress1, 150);
        assertEquals(200, exchAccount.getValue(accountAddress1), EPSILON);
        assertEquals(200, exchAccount.getTotalValue(), EPSILON);

        exchAccount.withdraw(accountAddress1, 100);
        assertEquals(100, exchAccount.getValue(accountAddress1), EPSILON);
        assertEquals(100, exchAccount.getTotalValue(), EPSILON);
        assertEquals(exchAccount.getTotalValue(), exchAccount.computeTotalValue(), EPSILON);

    }

    @Test
    public void testTransfer() throws TransactionFailedException {
        ExchangeAccount exchAccount = new ExchangeAccount("EUR");
        long accountAddress1 = 1234567L;
        exchAccount.deposit(accountAddress1, 50);
        exchAccount.deposit(accountAddress1, 150);
        long accountAddress2 = 7654321L;
        exchAccount.deposit(accountAddress2, 100);
        assertEquals(300, exchAccount.getTotalValue(), EPSILON);
        // exchAccount.transfer(accountAddress1, accountAddress2, 100);
        // assertEquals(100, exchAccount.getValue(accountAddress1), EPSILON);
        // assertEquals(200, exchAccount.getValue(accountAddress2), EPSILON);
        // assertEquals(300, exchAccount.getTotalValue(), EPSILON);
        // assertEquals(exchAccount.getTotalValue(), exchAccount.computeTotalValue(), EPSILON);
    }

}
