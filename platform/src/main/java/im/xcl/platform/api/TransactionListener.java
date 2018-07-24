package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;

public interface TransactionListener {
    void startBatch(StartBatch startBatch);

    void endBatch();
}
