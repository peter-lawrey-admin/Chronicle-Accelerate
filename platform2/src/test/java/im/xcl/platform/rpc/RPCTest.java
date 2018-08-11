package im.xcl.platform.rpc;

import im.xcl.platform.api.Verifier;
import im.xcl.platform.dto.Verification;
import im.xcl.platform.util.DtoParserBuilder;
import im.xcl.platform.verification.VanillaVerifyIP;
import net.openhft.chronicle.core.Mocker;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RPCTest {
    @Test
    public void testVerify() throws IOException, InterruptedException {
        KeySet zero = new KeySet(0);
        KeySet one = new KeySet(1);

        DtoParserBuilder<Verifier> protocol = new DtoParserBuilder<Verifier>()
                .addProtocol(1, Verifier.class);
        RPCServer<Verifier> server = new RPCServer<>("test",
                9999,
                9999,
                zero.publicKey,
                zero.secretKey,
                Verifier.class,
                protocol,
                VanillaVerifyIP::new);

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Verifier verifier = Mocker.queuing(Verifier.class, "", queue);
        RPCClient<Verifier, Verifier> client = new RPCClient<>(
                "test",
                "localhost",
                9999,
                zero.secretKey,
                Verifier.class,
                protocol.get(),
                verifier);

        Verification message = protocol.create(Verification.class);
        message.keyVerified(one.publicKey);
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
