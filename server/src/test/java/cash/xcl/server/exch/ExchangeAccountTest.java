package cash.xcl.server.exch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import cash.xcl.api.exch.CurrencyPair;

public class ExchangeAccountTest {

    private static final double EPSILON = Math.pow(10, -10);

    @Test
    public void totalValue() throws TransactionFailedException {
        CurrencyPair pair = new CurrencyPair("XCL", "EUR");
        ExchangeAccounts exchAccount = new ExchangeAccounts(pair);
        long accountAddress1 = 1234567L;
        long accountAddress2 = 12345678;
        long accountAddress3 = 12345678;
        Account baseAccount1 = exchAccount.getBaseAccount(accountAddress1, true);
        Account baseAccount2 = exchAccount.getBaseAccount(accountAddress2, true);
        Account baseAccount3 = exchAccount.getBaseAccount(accountAddress3, true);
        Account quoteAccount1 = exchAccount.getQuoteAccount(accountAddress1, true);
        Account quoteAccount2 = exchAccount.getQuoteAccount(accountAddress2, true);
        Account quoteAccount3 = exchAccount.getQuoteAccount(accountAddress3, true);
        baseAccount1.deposit(50);
        baseAccount2.deposit(100);
        baseAccount3.deposit(150);
        quoteAccount1.deposit(50);
        quoteAccount2.deposit(100);
        quoteAccount3.deposit(150);
        assertEquals(300, exchAccount.totalValueForBase(), EPSILON);
        assertEquals(300, exchAccount.totalValueForQuote(), EPSILON);


    }


    @Test
    public void defaultCreation() throws TransactionFailedException {
        CurrencyPair pair = new CurrencyPair("XCL", "EUR");
        ExchangeAccounts exchAccount = new ExchangeAccounts(pair);
        assertNull(exchAccount.getBaseAccount(12345678, false));
        assertNull(exchAccount.getQuoteAccount(12345678, false));
        assertNull(exchAccount.getBaseAccount(12345679, false));
        assertNull(exchAccount.getQuoteAccount(12345679, false));

        assertNotNull(exchAccount.getBaseAccount(12345678, true));
        assertNotNull(exchAccount.getQuoteAccount(12345678, false));

        assertNotNull(exchAccount.getQuoteAccount(12345679, true));
        assertNotNull(exchAccount.getBaseAccount(12345679, false));
    }


    @Test
    public void currencyPair() {
        CurrencyPair pair = new CurrencyPair("XCL", "EUR");
        ExchangeAccounts exchAccount = new ExchangeAccounts(pair);
        assertEquals(pair, exchAccount.getCurrencyPair());
    }


}
