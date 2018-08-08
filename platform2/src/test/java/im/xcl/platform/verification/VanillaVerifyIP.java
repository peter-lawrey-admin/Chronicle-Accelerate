package im.xcl.platform.verification;

import im.xcl.platform.api.MessageRouter;
import im.xcl.platform.api.Verifier;
import im.xcl.platform.dto.Invalidation;
import im.xcl.platform.dto.Verification;
import net.openhft.chronicle.bytes.BytesStore;

import java.util.HashMap;
import java.util.Map;

import static im.xcl.platform.api.MessageRouter.DEFAULT_CONNECTION;

public class VanillaVerifyIP implements Verifier {
    private final MessageRouter<Verifier> client;
    private final Map<BytesStore, Verification> verifyMap = new HashMap<>();

    public VanillaVerifyIP(MessageRouter<Verifier> client) {
        this.client = client;
    }

    @Override
    public void onConnection() {
        for (Verification verify : verifyMap.values()) {
            client.to(DEFAULT_CONNECTION)
                    .verification(verify);
        }
    }

    @Override
    public void verification(Verification verification) {
        Verification v2 = verification.deepCopy();
        verifyMap.put(v2.keyVerified(), v2);
    }

    @Override
    public void invalidation(Invalidation record) {
        verifyMap.remove(record.publicKey());
    }
}
