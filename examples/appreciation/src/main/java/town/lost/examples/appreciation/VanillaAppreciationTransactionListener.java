package town.lost.examples.appreciation;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.util.XCLUtil;
import town.lost.examples.appreciation.api.*;

public class VanillaAppreciationTransactionListener implements AppreciationTransactionListener {
    private final MessageRouter<AppreciationResultsListener> router;
    private final BalanceStore balanceStore;
    private final OnBalance onBalance = new OnBalance();

    public VanillaAppreciationTransactionListener(
            MessageRouter<AppreciationResultsListener> router,
            BalanceStore balanceStore) {
        this.router = router;
        this.balanceStore = balanceStore;
    }

    @Override
    public void openingBalance(OpeningBalance openingBalance) {
        balanceStore.setBalance(XCLUtil.toAddress(openingBalance.account()), openingBalance.amount());
    }

    @Override
    public void give(Give give) {
        long fromKey = give.address();
        long toKey = give.receiverAddress();
        if (balanceStore.subtractBalance(fromKey, give.amount())) {
            balanceStore.addBalance(toKey, give.amount());

            router.to(fromKey)
                    .onBalance(onBalance.init(fromKey, balanceStore.getBalance(fromKey)));
            router.to(toKey)
                    .onBalance(onBalance.init(toKey, balanceStore.getBalance(toKey)));
        }
    }
}
