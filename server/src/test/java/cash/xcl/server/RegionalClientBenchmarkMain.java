package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLClient;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/*
-XX:+UnlockCommercialFeatures
-XX:+FlightRecorder
-XX:+UnlockDiagnosticVMOptions
-XX:+DebugNonSafepoints
-XX:StartFlightRecording=name=test,filename=test.jfr,dumponexit=true,settings=profile
-DfastJava8IO=true

Intel(R) Core(TM) i7-7820X CPU @ 3.60GHz, Centos 7 with linux 4.12
TBA


Intel i7-7820X, Windows 10, 64 GB memory.
as a core service

TODO The client and server generate different keys. Client needs to send the server the public keys and vise-versa
*/

public class RegionalClientBenchmarkMain {
    static final boolean INTERNAL = Boolean.getBoolean("internal");
    static final String HOST = System.getProperty("host", "localhost");
    private int serverAddress = 10001;
    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        System.out.println("internal= " + INTERNAL);
        RegionalClientBenchmarkMain benchmarkMain = null;
        try {
            int iterations = 3;
            int transfers = INTERNAL ? 1_000_000 : 20_000;
            int total = iterations * transfers;
            benchmarkMain = new RegionalClientBenchmarkMain(10, 8);
            String oneThread = benchmarkMain.benchmark(iterations, 1, transfers);
            String twoThreads = benchmarkMain.benchmark(iterations, 2, transfers);
            String fourThreads = benchmarkMain.benchmark(iterations, 4, transfers);
            String eightThreads = benchmarkMain.benchmark(iterations, 8, transfers);
            System.out.println("Total number of messages per benchmark = " + total);
            System.out.println("Including signing and verifying = " + !INTERNAL);
            System.out.println("benchmark - oneThread = " + oneThread + " messages per second");
            System.out.println("benchmark - twoThreads = " + twoThreads + " messages per second");
            System.out.println("benchmark - fourThreads = " + fourThreads + " messages per second");
            System.out.println("benchmark - eightThreads = " + eightThreads + " messages per second");
        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
            System.exit(0);
        }
    }


    public RegionalClientBenchmarkMain(int iterations, int clientThreads) {

        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

        for (int iterationNumber = 0; iterationNumber < iterations; iterationNumber++) {
            for (int s = 0; s < clientThreads; s++) {
                final int sourceAddress = (iterationNumber * 100) + s + 1;
                final int destinationAddress = sourceAddress + 1000000;

                XCLClient client = null;
                try {
                    AtomicInteger count = new AtomicInteger();
                    client = new XCLClient("client", HOST,
                            serverAddress, sourceAddress, secretKey,
                            new RegionalClientBenchmarkMain.MyWritingAllMessages(count));

                    // register public keys
                    client.internal(true);
                    client.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0, sourceAddress, publicKey));
                    client.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0, destinationAddress, publicKey));

                    client.internal(INTERNAL);
                    sendOpeningBalance(client, sourceAddress, sourceAddress);
                    sendOpeningBalance(client, sourceAddress, destinationAddress);
                } finally {
                    // no need to wait for a response
                    // as the server does not send a reply for OpeningBalanceEvents
                    Closeable.closeQuietly(client);
                    //client.close();
                }
            }

        }
    }

    static void sendOpeningBalance(XCLClient client, int sourceAddress, int destinationAddress) {
        final OpeningBalanceEvent obe1 = new OpeningBalanceEvent(sourceAddress,
                1,
                destinationAddress,
                "USD",
                1000);
        client.openingBalanceEvent(obe1);
    }

    private String benchmark(int iterations, int clientThreads, int transfers) throws InterruptedException, ExecutionException {

        Thread.sleep(2000);

        System.out.println(" STARTING BENCHMARK TEST **********************************************");
        // number of messages = msgs * number of iterations
        // number of messages = 10000 * 10 = 100,000
        double allIterationsTotalTimeSecs = 0;
        double totalSentTime = 0;
        for (int iterationNumber = 0; iterationNumber < iterations; iterationNumber++) {
            long start = System.nanoTime();
            ExecutorService service = Executors.newFixedThreadPool(clientThreads);
            BlockingQueue<String> sent = new LinkedBlockingQueue<>(10);// todo: with 4 we get: java.lang.IllegalStateException: Queue full
            List<Future> futures = new ArrayList<>();
            long sentStart = System.nanoTime();
            for (int s = 0; s < clientThreads; s++) {
                final int sourceAddress = (iterationNumber * 100) + s + 1;
                final int destinationAddress = sourceAddress + 1000000;

                futures.add(service.submit((Callable<Void>) () -> {
                    try {
                        AtomicInteger count = new AtomicInteger();
                        AllMessages queuing = new MyWritingAllMessages(count);
                        XCLClient client = new XCLClient("client", HOST, this.serverAddress, sourceAddress, secretKey, queuing);
                        client.internal(INTERNAL);
                        client.subscriptionQuery(new SubscriptionQuery(sourceAddress, 0));
                        TransferValueCommand tvc1 = new TransferValueCommand(sourceAddress, 0, destinationAddress, 1e-9, "USD", "");
                        int c = 0;
                        for (int i = 0; i < transfers; i += clientThreads) {
                            client.transferValueCommand(tvc1);
                            if (++c > 10000 && c % 1000 == 0)
                                Jvm.pause(1);
                        }
                        sent.add("DONE");
                        long last = System.currentTimeMillis() + 1000;
                        for (int i = 0; i < transfers; i += clientThreads) {
                            while (count.get() <= 0) {
                                if (System.currentTimeMillis() > last + 1000) {
                                    System.out.println("pause " + i);
                                    last = System.currentTimeMillis();
                                }
                                Jvm.pause(10);
                            }
                            count.decrementAndGet();
                        }
                        //client.close();
                        Closeable.closeQuietly(client);
                        // +2 for the opening balances.
                        assertEquals(1, count.get(), 1);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return null;
                }));
            }
            for (Future future : futures)
                sent.take();
            double sentTime = (System.nanoTime() - sentStart) / 1e9;
            totalSentTime += sentTime;
            for (Future future : futures)
                future.get();

            double time = (System.nanoTime() - start) / 1e9;

            System.out.printf("Iteration %d - Throughput: %,d sustained, %,d burst messages per sec%n",
                    iterationNumber, (int) (transfers / time), (int) (transfers / sentTime));
            allIterationsTotalTimeSecs += time;
            service.shutdown();
        }

        int sentAverage = (int) (transfers * iterations / totalSentTime);
        int average = (int) (transfers * iterations / allIterationsTotalTimeSecs);

        System.out.printf("Average Throughput after sending %d messages (%d messages * %d times) using %d client threads = %,d / sec, %,d /sec burst%n",
                transfers * iterations,
                transfers,
                iterations,
                clientThreads,
                average,
                sentAverage);


        return average + " sustained, " + sentAverage + " burst";
    }


    private static class MyWritingAllMessages extends WritingAllMessages {
        private final AtomicInteger count;

        public MyWritingAllMessages(AtomicInteger count) {
            this.count = count;
        }

        @Override
        public WritingAllMessages to(long addressOrRegion) {
            return this;
        }

        @Override
        public void write(SignedBinaryMessage message) {
            count.incrementAndGet();
        }

        @Override
        public void close() {

        }
    }


}
