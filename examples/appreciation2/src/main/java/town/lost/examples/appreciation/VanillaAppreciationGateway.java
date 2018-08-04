package town.lost.examples.appreciation;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.api.StartBatch;
import net.openhft.chronicle.bytes.BytesStore;
import town.lost.examples.appreciation.api.*;


public class VanillaAppreciationGateway implements AppreciationGateway {
    private final MessageRouter<AppreciationResultsListener> client;
    private final AppreciationTransactionListener blockchain;
    private final BalanceStore balanceStore;

    private final OnBalance onBalance = new OnBalance();
    private final OnError onError = new OnError();
    private StartBatch startBatch;

    public VanillaAppreciationGateway(
            MessageRouter<AppreciationResultsListener> client,
            AppreciationTransactionListener blockchain,
            BalanceStore balanceStore) {
        this.client = client;
        this.blockchain = blockchain;
        this.balanceStore = balanceStore;
    }

    @Override
    public void startBatch(StartBatch startBatch) {
        this.startBatch = startBatch;
    }

    @Override
    public void openingBalance(OpeningBalance openingBalance) {
        if (verifyServerNode(startBatch.batchKey()))
            blockchain.openingBalance(openingBalance);
    }

    private boolean verifyServerNode(BytesStore publicKey) {
        // allow anyone for now.
        return true;
    }

    @Override
    public void queryBalance() {
        BytesStore clientKey = startBatch.batchKey();
        AppreciationResultsListener listener = client.to(clientKey);
        double amount = balanceStore.getBalance(clientKey);
        if (Double.isNaN(amount)) {
            onError.init(clientKey,
                    startBatch.batchTimeUS(),
                    "Cannot query balance: Account doesn't exist");
            listener.onError(onError);
        } else {
            listener.onBalance(onBalance);
        }
    }

    @Override
    public void give(Give give) {
        if (give.amount() < 0) {
            BytesStore clientKey = startBatch.batchKey();
            AppreciationResultsListener listener = client.to(clientKey);
            onError.init(clientKey,
                    startBatch.batchTimeUS(),
                    "Cannot give a negative amount");
            listener.onError(onError);
            return;
        }
        double amount = balanceStore.getBalance(startBatch.batchKey());
        if (Double.isNaN(amount)) {
            BytesStore clientKey = startBatch.batchKey();
            AppreciationResultsListener listener = client.to(clientKey);
            onError.init(startBatch.batchKey(),
                    startBatch.batchTimeUS(),
                    "Cannot give balance: Account doesn't exist");
            listener.onError(onError);
            return;
        }

        blockchain.give(give);
    }
}
