package cash.xcl.server.mock;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.WritingAllMessages;
import cash.xcl.api.tcp.XCLClient;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ClientProcessTest {

    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);

    @Test
    public void test() {
        ServerJVM server = null;
        XCLClient client = null;
        try {
            int sourceAddress = 1;
            Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

            server = new ServerJVM(ServerJVM.DEFAULT_SERVER_ADDRESS, secretKey,1000, 50, sourceAddress, publicKey);
            server.register(sourceAddress, publicKey);

            AtomicInteger count = new AtomicInteger();
            AllMessages listener = new MyWritingAllMessages(count);
            client = new XCLClient("client", "localhost", ServerJVM.DEFAULT_SERVER_ADDRESS, sourceAddress, secretKey,
                    listener, true);
                    //.internal(false);

            client.createNewAddressCommand(new CreateNewAddressCommand(sourceAddress, 1L, publicKey, "usny"));

            Jvm.pause(100);

            OpeningBalanceEvent obe1 = new OpeningBalanceEvent(sourceAddress, 1, sourceAddress, "USD", 1000);
            client.openingBalanceEvent(obe1);

            Jvm.pause(100);

            int destinationAddress = 2;
            OpeningBalanceEvent obe2 = new OpeningBalanceEvent(sourceAddress, 1, destinationAddress, "USD", 1000);
            client.openingBalanceEvent(obe2);

            Jvm.pause(100);

            TransferValueCommand tvc = new TransferValueCommand(sourceAddress, 3, destinationAddress, 1.23, "USD", "init");
            client.transferValueCommand(tvc);

            Jvm.pause(1000);

            System.out.println("count is " + count);

            assertEquals(2, count.get(), 1);

        } finally {
            Jvm.pause(1000);

            // TODO
            // if we close here now , we will get Interrupted exceptions in the voting thread
            // but if we dont close , we will get binding exceptions later?

            //net.openhft.chronicle.core.io.Closeable.closeQuietly(server);
        }

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
            System.out.println("message received " + message);
            count.incrementAndGet();
        }

        @Override
        public void transferValueEvent(TransferValueEvent transferValueEvent) {
            System.out.println("message received " + transferValueEvent);
            count.incrementAndGet();
        }


        @Override
        public void close() {

        }
    }

}