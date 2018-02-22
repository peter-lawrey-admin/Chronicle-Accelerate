package cash.xcl.server.exch;

final class UserAccounts {
    private final Account baseCurrencyAccount;

    private final Account quoteCurrencyAccount;

    UserAccounts(Account baseCurrencyAccount, Account quoteCurrencyAccount) {
        this.baseCurrencyAccount = baseCurrencyAccount;
        this.quoteCurrencyAccount = quoteCurrencyAccount;
    }

    UserAccounts() {
        this(new Account(), new Account());
    }

    Account getBaseCurrencyAccount() {
        return baseCurrencyAccount;
    }

    Account getQuoteCurrencyAccount() {
        return quoteCurrencyAccount;
    }

}
