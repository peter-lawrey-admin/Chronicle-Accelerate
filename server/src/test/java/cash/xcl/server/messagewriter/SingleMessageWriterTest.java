package cash.xcl.server.messagewriter;


import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.Gateway;
import cash.xcl.server.VanillaGateway;
import junit.framework.TestCase;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Ignore
public class SingleMessageWriterTest extends TestCase {
    private XCLServer server;
    private Gateway gateway;
    final private int serverAddress = 10001;
    final private int sourceAddress = 1;
    final private int destinationAddress = 1;
    private TransferValueCommand tvc1;
    public static int NUMBER_OF_MESSAGES = 100_000;
    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
    private static boolean isSetUp = false;

    @Ignore
    private void init() throws Exception {
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
        long[] clusterAddresses = {serverAddress};
        this.gateway = VanillaGateway.newGateway(serverAddress, "gb1dn", clusterAddresses,
                1000, 40,
                TransactionBlockEvent._2_MB);
        this.server = new XCLServer("one", serverAddress, serverAddress, secretKey, gateway);
        gateway.start();
        tvc1 = new TransferValueCommand(sourceAddress, 0, destinationAddress, 1e-9, "USD", "");
    }

    @Ignore
    public void messageWriterAtomicReference() throws ExecutionException, InterruptedException {
        SingleMessageWriterAtomicReference singleMessageWriter = new SingleMessageWriterAtomicReference(server);
        Future future = singleMessageWriter.start();
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            singleMessageWriter.write(tvc1);
        }
        future.get();
    }

    @Ignore
    public void messageWriterAtomicBoolean() throws ExecutionException, InterruptedException {
        SingleMessageWriterAtomicBoolean singleMessageWriter3 = new SingleMessageWriterAtomicBoolean(server);
        Future future = singleMessageWriter3.start();
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            singleMessageWriter3.write(tvc1);
        }
        future.get();
    }

    @Ignore
    @Test
    public void testSpeed() throws Exception {

        init();

        long time0 = System.nanoTime();
        messageWriterAtomicBoolean();
        long time1 = System.nanoTime();
        messageWriterAtomicReference();
        long time2 = System.nanoTime();

        long testMessageWriterAtomicBoolean = time1 - time0;
        long testMessageWriterAtomicReference = time2 - time1;

        System.out.printf("old -> messageWriterAtomicBoolean   %,f seconds%n" , (testMessageWriterAtomicBoolean / 1e9) );
        System.out.printf("new -> messageWriterAtomicReference %,f seconds%n" , (testMessageWriterAtomicReference / 1e9) );

        //assert messageWriterAtomicReference < messageWriterAtomicBoolean;
    }

}