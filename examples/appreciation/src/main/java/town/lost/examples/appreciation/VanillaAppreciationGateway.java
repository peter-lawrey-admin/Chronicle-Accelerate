package town.lost.examples.appreciation;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.dto.ApplicationError;
import town.lost.examples.appreciation.api.*;


public class VanillaAppreciationGateway implements AppreciationGateway {
    private final MessageRouter<AppreciationResultsListener> client;
    private final AppreciationTransactionListener blockchain;
    private final BalanceStore balanceStore;

    private final OnBalance onBalance = new OnBalance();
    private final ApplicationError error = new ApplicationError(1, 1);

    public VanillaAppreciationGateway(
            MessageRouter<AppreciationResultsListener> client,
            AppreciationTransactionListener blockchain,
            BalanceStore balanceStore) {
        this.client = client;
        this.blockchain = blockchain;
        this.balanceStore = balanceStore;
    }

    @Override
    public void openingBalance(OpeningBalance openingBalance) {
        if (verifyServerNode(openingBalance.address()))
            blockchain.openingBalance(openingBalance);
    }

    private boolean verifyServerNode(long address) {
        // allow anyone for now.
        return true;
    }

    @Override
    public void queryBalance(QueryBalance queryBalance) {
        long account = queryBalance.account();
        AppreciationResultsListener listener = client.to(account);
        double amount = balanceStore.getBalance(account);
        if (Double.isNaN(amount)) {
            error.init(queryBalance,
                    "Cannot query balance: Account doesn't exist");
            listener.applicationError(error);
        } else {
            listener.onBalance(onBalance);
        }
    }

    @Override
    public void give(Give give) {
        long address = give.address();
        if (give.amount() < 0) {
            AppreciationResultsListener listener = client.to(address);
            error.init(give,
                    "Cannot give a negative amount");
            listener.applicationError(error);
            return;
        }
        double amount = balanceStore.getBalance(address);
        if (Double.isNaN(amount)) {
            AppreciationResultsListener listener = client.to(address);
            error.init(give,
                    "Cannot give balance: Account doesn't exist");
            listener.applicationError(error);
            return;
        }

        blockchain.give(give);
    }
}
