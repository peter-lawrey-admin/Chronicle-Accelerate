package town.lost.examples.verifyip;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.api.StartBatch;
import net.openhft.chronicle.bytes.BytesStore;
import town.lost.examples.verifyip.api.Invalidation;
import town.lost.examples.verifyip.api.Verify;
import town.lost.examples.verifyip.api.VerifyIP;
import town.lost.examples.verifyip.api.VerifyIPResponse;

import java.util.HashMap;
import java.util.Map;

import static im.xcl.platform.api.MessageRouter.DEFAULT_CONNECTION;

public class VanillaVerifyIP implements VerifyIP {
    private final MessageRouter<VerifyIPResponse> client;
    private final Map<BytesStore, Verify> verifyMap = new HashMap<>();

    private StartBatch startBatch;

    public VanillaVerifyIP(MessageRouter<VerifyIPResponse> client) {
        this.client = client;
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
    public void onConnection() {
        for (Verify verify : verifyMap.values()) {
            client.to(DEFAULT_CONNECTION)
                    .onVerify(verify);
        }
    }

    @Override
    public void verify(Verify verify) {
        verifyMap.put(startBatch.batchKey().copy(), verify.deepCopy());
    }

    @Override
    public void invalidation(Invalidation record) {
        verifyMap.remove(startBatch.batchKey());
    }
}
