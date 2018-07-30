package town.lost.examples.appreciation;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.api.StartBatch;
import net.openhft.chronicle.bytes.BytesStore;
import town.lost.examples.appreciation.api.*;

public class VanillaAppreciationTransactionListener implements AppreciationTransactionListener {
    private final MessageRouter<AppreciationResultsListener> router;
    private final BalanceStore balanceStore;
    private final OnBalance onBalance = new OnBalance();
    private StartBatch startBatch;

    public VanillaAppreciationTransactionListener(
            MessageRouter<AppreciationResultsListener> router,
            BalanceStore balanceStore) {
        this.router = router;
        this.balanceStore = balanceStore;
    }

    @Override
    public void startBatch(StartBatch startBatch) {
        this.startBatch = startBatch;
    }

    @Override
    public void endBatch() {
        startBatch = null;
    }

    @Override
    public void openingBalance(OpeningBalance openingBalance) {
        balanceStore.setBalance(openingBalance.publicKey(), openingBalance.amount());
    }

    @Override
    public void give(Give give) {
        BytesStore fromKey = startBatch.batchKey();
        BytesStore toKey = give.publicKey();
        if (balanceStore.subtractBalance(fromKey, give.amount())) {
            balanceStore.addBalance(toKey, give.amount());

            router.to(fromKey)
                    .onBalance(onBalance.init(fromKey, balanceStore.getBalance(fromKey)));
            router.to(toKey)
                    .onBalance(onBalance.init(toKey, balanceStore.getBalance(toKey)));
        }
    }
}
