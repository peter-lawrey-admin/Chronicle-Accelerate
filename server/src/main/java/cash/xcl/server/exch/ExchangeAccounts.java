package cash.xcl.server.exch;

import java.util.HashMap;

import cash.xcl.api.exch.CurrencyPair;
import net.openhft.chronicle.core.annotation.SingleThreaded;

@SingleThreaded
class ExchangeAccounts {
    private final CurrencyPair currencyPair;

    private final HashMap<Long, UserAccounts> accounts = new HashMap<>();

    ExchangeAccounts(CurrencyPair currencyPair) {
        this.currencyPair = currencyPair;
    }

    Account getBaseAccount(long accountAddress, boolean create) {
        UserAccounts userAccounts = accounts.get(accountAddress);
        if (userAccounts == null) {
            if (create) {
                userAccounts = new UserAccounts();
                accounts.put(accountAddress, userAccounts);
            } else {
                return null;
            }
        }
        return userAccounts.getBaseCurrencyAccount();
    }

    Account getQuoteAccount(long accountAddress, boolean create) {
        UserAccounts userAccounts = accounts.get(accountAddress);
        if (userAccounts == null) {
            if (create) {
                userAccounts = new UserAccounts();
                accounts.put(accountAddress, userAccounts);
            } else {
                return null;
            }
        }
        return userAccounts.getQuoteCurrencyAccount();
    }


    double totalValueForBase() {
        return accounts.values().stream().map((a) -> a.getBaseCurrencyAccount().money()).reduce(0D, (a, b) -> a + b);
    }

    double totalValueForQuote() {
        return accounts.values().stream().map((a) -> a.getQuoteCurrencyAccount().money()).reduce(0D, (a, b) -> a + b);
    }

    CurrencyPair getCurrencyPair() {
        return currencyPair;
    }


}
