package im.xcl.platform.rpc;

import im.xcl.platform.api.Verifier;
import im.xcl.platform.dto.Verification;
import im.xcl.platform.util.DtoParserBuilder;
import im.xcl.platform.verification.VanillaVerifyIP;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RPCTest {
    @Test
    public void testVerify() throws IOException, InterruptedException {
        Bytes privateKey = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
        privateKey.zeroOut(0, 32);
        privateKey.writeSkip(32);
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Bytes<Void> privateKey1 = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);

        Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);

        DtoParserBuilder<Verifier> protocol = new DtoParserBuilder<Verifier>()
                .addProtocol(1, Verifier.class);
        RPCServer<Verifier> server = new RPCServer<>("test",
                9999,
                9999,
                publicKey,
                secretKey,
                protocol,
                VanillaVerifyIP::new);

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Verifier verifier = Mocker.queuing(Verifier.class, "", queue);
        RPCClient<Verifier, Verifier> client = new RPCClient<>(
                "test",
                "localhost",
                9999,
                secretKey,
                Verifier.class,
                protocol.get(),
                verifier);

        Verification message = protocol.create(Verification.class);
        client.write(message);
        while (queue.size() < 1)
            Thread.sleep(100);
        for (String s : queue) {
            System.out.println(s);
        }
        client.close();
        server.close();
    }
}
