package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.VanillaBytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;

public class RegionalServerBenchmarkMain {
    static XCLServer server;

    public static void main(String[] args) throws IOException, InterruptedException {
        VanillaBytes<Void> secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        secretKey.writeSkip(Ed25519.SECRET_KEY_LENGTH);
        server = new XCLServer("regional", 12345, 0, secretKey, new RegionalServer());

        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("localhost", 12345));
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        AllMessages queuing = Mocker.queuing(AllMessages.class, "", queue);
        XCLClient client = new XCLClient("client", addresses, 0, secretKey, queuing);
        client.subscriptionQuery(new SubscriptionQuery(0, 0));
        for (int t = 0; t < 10; t++) {
            int msgs = 10000;
            long start = System.nanoTime();
            for (int i = 0; i < msgs; i++)
                client.transferValueCommand(new TransferValueCommand(0, 0, 0, 1, "XCL", ""));
            for (int i = 0; i < msgs * 2; i++)
                queue.take();
            long time = System.nanoTime() - start;
            System.out.printf("Throughput: %,d / sec%n", (int) (msgs * 1e9 / time));
            Jvm.pause(100);
            assertEquals(0, queue.size());
        }

        client.close();
        server.close();
    }
}
