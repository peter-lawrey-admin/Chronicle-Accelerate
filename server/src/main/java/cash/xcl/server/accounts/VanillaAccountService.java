package cash.xcl.server.accounts;

import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.util.XCLBase32;
import cash.xcl.util.XCLLongObjMap;
import net.openhft.chronicle.core.annotation.NotNull;
import net.openhft.chronicle.core.annotation.Nullable;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class VanillaAccountService extends AbstractAllMessages implements AccountService {

    // TODO Use LongKey to be more GC friendly.
    private final XCLLongObjMap<BalanceByCurrency> balances = XCLLongObjMap.withExpectedSize(BalanceByCurrency.class, 16);


    public VanillaAccountService() {
        // TODO ?
        super(-1);
    }

    @Override
    @Nullable
    public BalanceByCurrency balances(long address) {

        return balances.get(address);
    }


    @Override
    public BalanceByCurrency registerNewAccount(CreateNewAddressEvent createNewAddressEvent) {
        if (balances.get(createNewAddressEvent.address()) != null) {
            throw new IllegalArgumentException("Duplicate NewAccount for " + createNewAddressEvent.address());
        }
        return balances.computeIfAbsent(createNewAddressEvent.address(), BalanceByCurrency::new);
    }


    @Override
    public BalanceByCurrency setOpeningBalancesForAccount(OpeningBalanceEvent openingBalanceEvent) throws Exception {
        // the opening balance of an account can only be set once
        if (balances(openingBalanceEvent.address()) == null) {
            BalanceByCurrency balanceByCurrency = balances.computeIfAbsent(openingBalanceEvent.address(), BalanceByCurrency::new);
            balanceByCurrency.setBalances(openingBalanceEvent.balances());
            return balanceByCurrency;
        } else {
            throw new IllegalArgumentException("Unable to set opening balance. Balance is already set for address " + openingBalanceEvent.address());
        }
    }

    @Override
    public void transfer(@NotNull final TransferValueCommand tvc) {
        long sourceAddress = tvc.sourceAddress();
        long destAddress = tvc.toAddress();
        String errorMsg = null;
        if (sourceAddress == destAddress) {
            errorMsg = "source and destination addresses are the same " + XCLBase32.encode(sourceAddress);
        } else {
            BalanceByCurrency sourceBalanceByCurrency = balances(sourceAddress);
            BalanceByCurrency destinationBalanceByCurrency = balances(destAddress);
            if (sourceBalanceByCurrency == null) {
                errorMsg = "no balance records for source address " + XCLBase32.encode(sourceAddress);
            } else if (destinationBalanceByCurrency == null) {
                errorMsg = "no balance records for destination address " + XCLBase32.encode(destAddress);
            } else {
                double sourceAccountBalance = sourceBalanceByCurrency.getBalance(tvc.currency());
                if (tvc.amount() > sourceAccountBalance) {
                    errorMsg = "trying to transfer " + tvc.currencyStr() + tvc.amount() + " from account: " + XCLBase32.encode(sourceAddress) + " but balance is only: " + sourceAccountBalance;
                } else if (tvc.amount() <= 0) {
                    errorMsg = "amount is negative or zero : " + tvc.amount();
                } else {
                    // NOTE: this code is not thread-safe, but we are only running in 1 thread, so it's ok.
                    sourceBalanceByCurrency.setBalance(tvc.currency(),
                            sourceAccountBalance - tvc.amount());
                    double destinationAccountBalance = destinationBalanceByCurrency.getBalance(tvc.currency());
                    destinationBalanceByCurrency.setBalance(tvc.currency(),
                            destinationAccountBalance + tvc.amount());
                }
            }
        }
        if (errorMsg != null)
            throw new IllegalArgumentException("Invalid transfer: " + errorMsg);
    }

    // only for testing purposes
    // note - access to balances is not thread-safe
    @Override
    public void print() {
        SortedMap<Long, BalanceByCurrency> map = new TreeMap<>();
        balances.forEach(map::put);
        for (Map.Entry<Long, BalanceByCurrency> entry : map.entrySet()) {
            System.out.print("Account Number = " + entry.getKey() + " ");
            BalanceByCurrency balanceByCurrency = entry.getValue();
            balanceByCurrency.print();
            System.out.println();
        }
    }

}
