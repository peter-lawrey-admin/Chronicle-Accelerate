package cash.xcl.server.exch;

import java.util.HashMap;
import java.util.Map;

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

    Map<String, Double> getBalance(long accountAddress) {
        UserAccounts userAccounts = accounts.get(accountAddress);
        if (userAccounts == null) {
            return null;
        } else {
            Map<String, Double> balances = new HashMap<>(); // we should create a simple 2 keys map class...
            double baseAmount = userAccounts.getBaseCurrencyAccount().money();
            if (baseAmount != 0) {
                balances.put(currencyPair.getBaseCurrency(), baseAmount);
            }
            double quoteAmount = userAccounts.getQuoteCurrencyAccount().money();
            if (quoteAmount != 0) {
                balances.put(currencyPair.getQuoteCurrency(), quoteAmount);
            }
            return balances;
        }
    }

}
