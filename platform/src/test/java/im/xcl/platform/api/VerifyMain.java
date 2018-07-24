package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.time.TimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.TextWire;

public class VerifyMain {

    public static void main(String[] args) {
        TextWire wire = new TextWire(Bytes.elasticHeapByteBuffer(128));
        AllListeners writer = wire.methodWriter(AllListeners.class);
        StartBlock startBlock = new StartBlock();

        Bytes<Void> blockKey = Ed25519.allocateSecretKey();
        Bytes<Void> blockPublicKey = Ed25519.allocatePublicKey();
        Ed25519.generatePublicAndSecretKey(blockPublicKey, blockKey);
        startBlock.blockKey = blockKey;
        startBlock.blockTimeUS = TimeProvider.get().currentTimeMicros();

        writer.startBlock(startBlock);

        StartBatch startBatch = new StartBatch();
        Bytes<Void> batchKey = Ed25519.allocateSecretKey();
        Bytes<Void> batchPublicKey = Ed25519.allocatePublicKey();
        Ed25519.generatePublicAndSecretKey(batchPublicKey, batchKey);
        startBatch.batchKey = batchKey;
        startBatch.batchTimeUS = TimeProvider.get().currentTimeMicros();

        writer.startBatch(startBatch);

        writer.verified(new Verified(blockPublicKey));
        writer.verified(new Verified(batchPublicKey));

        writer.endBatch();
        writer.endBlock();

        System.out.println(wire);
    }
}
