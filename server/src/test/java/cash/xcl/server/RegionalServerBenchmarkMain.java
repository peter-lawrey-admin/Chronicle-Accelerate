package cash.xcl.server;

public class RegionalServerBenchmarkMain {

   /* public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        VanillaBytes<Void> secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        secretKey.writeSkip(Ed25519.SECRET_KEY_LENGTH);
        XCLServer server = new XCLServer("regional", 12345, 0, secretKey, new RegionalServer());

        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("localhost", 12345));
        int threads = 1;

        for (int t = 0; t < 10; t++) {
            int msgs = 10000;
            long start = System.nanoTime();
            TransferValueCommand transferValueCommand = new TransferValueCommand(0, 0, 0, 1, "XCL", "");
            ExecutorService service = Executors.newFixedThreadPool(threads);
            List<Future> futures = new ArrayList<>();
            for (int s = 0; s < threads; s++) {
                futures.add(service.submit((Callable<Void>) () -> {
                    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
                    AllMessages queuing = Mocker.queuing(AllMessages.class, "", queue);
                    XCLClient client = new XCLClient("client", addresses, 0, secretKey, queuing);
                    client.subscriptionQuery(new SubscriptionQuery(0, 0));
                    for (int i = 0; i < msgs; i += threads)
                        client.transferValueCommand(transferValueCommand);
                    for (int i = 0; i < msgs * 2; i += threads)
                        queue.take();
                    client.close();
                    assertEquals(0, queue.size());
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
    }*/
}
