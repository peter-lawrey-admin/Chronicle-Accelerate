package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.api.util.XCLBase32;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class RegionalServerBenchmarkMain {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        Bytes publicKey = Ed25519.allocatePublicKey();
        Bytes secretKey = Ed25519.allocateSecretKey();
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
        String region = "gb1nd";
        long regionAddress = XCLBase32.decode(region);
        long address = 1;
        VanillaGateway gateway = VanillaGateway.newGateway(address, region, new long[]{regionAddress});
        XCLServer server = new XCLServer(region, 12345, address, secretKey, gateway);

        // only after added to the server.
        gateway.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0, address, publicKey));

        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("localhost", 12345));
        int threads = 1;

        for (int t = 0; t < 10; t++) {
            int msgs = 10;
            long start = System.nanoTime();
            TransferValueCommand transferValueCommand = new TransferValueCommand(address, 0, address, 1, "XCL", "");
            ExecutorService service = Executors.newFixedThreadPool(threads);
            List<Future> futures = new ArrayList<>();
            for (int s = 0; s < threads; s++) {
                futures.add(service.submit((Callable<Void>) () -> {
                    try {
                        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
                        AllMessages queuing = Mocker.queuing(AllMessages.class, "", queue);
                        XCLClient client = new XCLClient("client", addresses, address, secretKey, queuing);
                        client.subscriptionQuery(new SubscriptionQuery(address, 0));
                        for (int i = 0; i < msgs; i += threads)
                            client.transferValueCommand(transferValueCommand);
                        for (int i = 0; i < msgs * 2; i += threads)
                            queue.take();
                        client.close();
                        assertEquals(0, queue.size());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return null;
                }));
            }
            for (Future future : futures) {
                future.get();
            }
            long time = System.nanoTime() - start;
            System.out.printf("Throughput: %,d / sec%n", (int) (msgs * 1e9 / time));
            service.shutdown();
        }

        server.close();
    }
}
