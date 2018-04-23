package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLClient;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

*/

public class RegionalClientForSplunk {


    private static final Logger log = LoggerFactory.getLogger(RegionalClientForSplunk.class);



    static final boolean INTERNAL = Boolean.getBoolean("internal");
    static final String HOST = System.getProperty("host", "localhost");
    private int serverAddress = 10001;
    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);

    // todo:
    // on Windows - more that 100 iterations will cause issues with libsodium.dll
    static int ITERATIONS = 100;

    // small number of transfers per iteration - so that we can update Splunk more often
    static int TRANSFERS_PER_ITERATION = 1_000;
    static int TOTAL_TRANSFERS = ITERATIONS * TRANSFERS_PER_ITERATION;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        log.info("internal= " + INTERNAL);
        RegionalClientForSplunk benchmarkMain = null;
        try {
            while(true) {
                benchmarkMain = new RegionalClientForSplunk(ITERATIONS, 8);
                String oneThread = benchmarkMain.benchmark(ITERATIONS, 1, TRANSFERS_PER_ITERATION);
                String twoThreads = benchmarkMain.benchmark(ITERATIONS, 2, TRANSFERS_PER_ITERATION);
                String fourThreads = benchmarkMain.benchmark(ITERATIONS, 4, TRANSFERS_PER_ITERATION);
                String eightThreads = benchmarkMain.benchmark(ITERATIONS, 8, TRANSFERS_PER_ITERATION);
                log.info("Total number of messages per benchmark = " + TOTAL_TRANSFERS);
                log.info("Including signing and verifying = " + !INTERNAL);
                log.info("benchmark - oneThread = " + oneThread + " messages per second");
                log.info("benchmark - twoThreads = " + twoThreads + " messages per second");
                log.info("benchmark - fourThreads = " + fourThreads + " messages per second");
                log.info("benchmark - eightThreads = " + eightThreads + " messages per second");
            }
        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
            System.exit(0);
        }
    }


    public RegionalClientForSplunk(int iterations, int clientThreads) {

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
                            new RegionalClientForSplunk.MyWritingAllMessages(count));

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

    private String benchmark(int iterations, int clientThreads, int transfersPerIteration) throws InterruptedException, ExecutionException {

        Thread.sleep(2000);

        log.info(" STARTING BENCHMARK TEST **********************************************");
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
                        int requests = 0;
                        for (int i = 0; i < transfersPerIteration; i += clientThreads) {
                            client.transferValueCommand(tvc1);
                            requests++;
                            if (++c > 10000 && c % 1000 == 0)
                                Jvm.pause(1);
                        }
                        sent.add("DONE");
                        long last = System.currentTimeMillis() + 1000;
                        int replies = 0;
                        for (int transferNumber = 0; transferNumber < transfersPerIteration; transferNumber += clientThreads) {
                            while (count.get() <= 0) {
                                if (System.currentTimeMillis() > last + 1000) {
                                    log.info("waiting, transferNumber={}, sourceAddress={}, requests={}, replies={}", transferNumber, sourceAddress,requests, replies);
                                    last = System.currentTimeMillis();
                                }
                                Jvm.pause(10);
                            }
                            replies++;
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



            log.info("Iteration={}, SustainedTransactionsPerSec={}, BurstTransactionsPerSec={}, clientThreads={}",
                    iterationNumber,
                    (int) (transfersPerIteration / time),
                    (int) (transfersPerIteration / sentTime),
                    clientThreads);

            allIterationsTotalTimeSecs += time;
            service.shutdown();
        }

        int sentAverage = (int) (transfersPerIteration * iterations / totalSentTime);
        int average = (int) (transfersPerIteration * iterations / allIterationsTotalTimeSecs);

        log.info("Benchmark for: " +
                        "clientThreads={}, " +
                        "iterations={} and " +
                        "transactionsPerIteration={}: " +
                        "TotalNumberOfTransactions={} " +
                        "AverageSUSTAINEDtransactionsPerSec={} " +
                        "AverageBURSTtransactionsPerSec={} ",
                clientThreads,
                iterations,
                transfersPerIteration,
                transfersPerIteration * iterations,
                average,
                sentAverage);


        return average + " transactions/sec sustained, " + sentAverage + " transactions/sec burst";
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
        public void write(SignedMessage message) {
            count.incrementAndGet();
        }

        @Override
        public void close() {

        }
    }


}
