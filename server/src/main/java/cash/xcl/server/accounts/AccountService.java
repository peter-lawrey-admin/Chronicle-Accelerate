package cash.xcl.server.accounts;

import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;
import cash.xcl.api.dto.TransferValueCommand;
import net.openhft.chronicle.core.annotation.NotNull;
import net.openhft.chronicle.core.annotation.Nullable;

public interface AccountService {

    @Nullable
    BalanceByCurrency balances(long address);

    @NotNull
    BalanceByCurrency setOpeningBalancesForAccount(OpeningBalanceEvent openingBalanceEvent) throws Exception;

    @NotNull
    BalanceByCurrency registerNewAccount(CreateNewAddressEvent createNewAddressEvent);

    void transfer(@NotNull TransferValueCommand transferValueCommand);

    // only for testing purposes
    void print();
}
