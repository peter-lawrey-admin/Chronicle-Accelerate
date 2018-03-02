package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class ThreeServerContainer {

    private final XCLServer one, two, three;
    private final XCLClient toOne, toTwo, toThree;
    private final BlockingQueue<String> oneQ, twoQ, threeQ;

    Bytes<Void> publicKey1 = Bytes.allocateElasticDirect();
    Bytes<Void> secretKey1 = Bytes.allocateElasticDirect();

    Bytes<Void> publicKey2 = Bytes.allocateElasticDirect();
    Bytes<Void> secretKey2 = Bytes.allocateElasticDirect();

    Bytes<Void> publicKey3 = Bytes.allocateElasticDirect();
    Bytes<Void> secretKey3 = Bytes.allocateElasticDirect();

    Bytes<Void> publicKey = Bytes.allocateElasticDirect();
    Bytes<Void> secretKey = Bytes.allocateElasticDirect();

    public ThreeServerContainer() throws IOException {
        Ed25519.generatePublicAndSecretKey(publicKey1, secretKey1);
        Ed25519.generatePublicAndSecretKey(publicKey2, secretKey2);
        Ed25519.generatePublicAndSecretKey(publicKey3, secretKey3);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

        long[] clusterAddresses = {10001, 10002, 10003};
        Gateway gateway1 = VanillaGateway.newGateway(10001, "gb1dn", clusterAddresses, 1000, 500);
        one = new XCLServer("one", 10001, 10001, secretKey1, gateway1);
        weeklySetup(gateway1);

        Gateway gateway2 = VanillaGateway.newGateway(10002, "gb1dn", clusterAddresses, 1000, 500);
        two = new XCLServer("two", 10002, 10002, secretKey2, gateway2);
        weeklySetup(gateway2);

        Gateway gateway3 = VanillaGateway.newGateway(10003, "gb1dn", clusterAddresses, 1000, 500);
        three = new XCLServer("two", 10003, 10003, secretKey3, gateway3);
        weeklySetup(gateway3);

        one.addTCPConnection(10002,
                new XCLClient("two", "localhost", 10002, 10001, secretKey1, gateway1));
        one.addTCPConnection(10003,
                new XCLClient("three", "localhost", 10003, 10001, secretKey1, gateway1));

        two.addTCPConnection(10001,
                new XCLClient("one", "localhost", 10001, 10002, secretKey2, gateway2));
        two.addTCPConnection(10003,
                new XCLClient("three", "localhost", 10003, 10002, secretKey2, gateway2));

        three.addTCPConnection(10002,
                new XCLClient("two", "localhost", 10002, 10003, secretKey3, gateway3));
        three.addTCPConnection(10001,
                new XCLClient("one", "localhost", 10001, 10003, secretKey3, gateway3));

        oneQ = new LinkedBlockingQueue<>();
        toOne = new XCLClient("one", "localhost", 10001, 1, secretKey,
                Mocker.queuing(AllMessages.class, "one ", oneQ));
        twoQ = new LinkedBlockingQueue<>();
        toTwo = new XCLClient("two", "localhost", 10002, 1, secretKey,
                Mocker.queuing(AllMessages.class, "two ", twoQ));
        threeQ = new LinkedBlockingQueue<>();
        toThree = new XCLClient("three", "localhost", 10003, 1, secretKey,
                Mocker.queuing(AllMessages.class, "three ", threeQ));
        gateway1.start();
        gateway2.start();
        gateway3.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Bytes<Void> publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        publicKey.writeSkip(Ed25519.PUBLIC_KEY_LENGTH);

        ThreeServerContainer tsc = new ThreeServerContainer();
        try {
            tsc.toOne.subscriptionQuery(new SubscriptionQuery(1, 0));
            tsc.toOne.createNewAddressCommand(
                    new CreateNewAddressCommand(1, 1, publicKey, "gb1nd"));
            String poll = tsc.oneQ.poll(10, TimeUnit.SECONDS);
            assertEquals("one createNewAddressEvent[!CreateNewAddressEvent {\n" +
                    "  sourceAddress: 10001,\n" +
                    "  eventTime: 0,\n" +
                    "  origSourceAddress: 1,\n" +
                    "  origEventTime: 1,\n" +
                    "  address: 0,\n" +
                    "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                    "}\n" +
                    "]", poll.replaceAll("eventTime: \\d+", "eventTime: 0"));

            tsc.toTwo.subscriptionQuery(new SubscriptionQuery(1, 0));
            tsc.toTwo.createNewAddressCommand(
                    new CreateNewAddressCommand(1, 2, publicKey, "gb1nd"));

            tsc.toThree.subscriptionQuery(new SubscriptionQuery(1, 0));
            tsc.toThree.createNewAddressCommand(
                    new CreateNewAddressCommand(1, 3, publicKey, "gb1nd"));
            poll = tsc.oneQ.poll(10, TimeUnit.SECONDS);
            assertEquals("one createNewAddressEvent[!CreateNewAddressEvent {\n" +
                    "  sourceAddress: 10001,\n" +
                    "  eventTime: 0,\n" +
                    "  origSourceAddress: 1,\n" +
                    "  origEventTime: 2,\n" +
                    "  address: 0,\n" +
                    "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                    "}\n" +
                    "]", poll.replaceAll("eventTime: \\d+", "eventTime: 0"));
            poll = tsc.oneQ.poll(10, TimeUnit.SECONDS);
            assertEquals("one createNewAddressEvent[!CreateNewAddressEvent {\n" +
                    "  sourceAddress: 10001,\n" +
                    "  eventTime: 0,\n" +
                    "  origSourceAddress: 1,\n" +
                    "  origEventTime: 3,\n" +
                    "  address: 0,\n" +
                    "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                    "}\n" +
                    "]", poll.replaceAll("eventTime: \\d+", "eventTime: 0"));


        } finally {
            Jvm.pause(1000);
            tsc.close();
        }
    }

    private void weeklySetup(Gateway gateway) {
        gateway.createNewAddressEvent(new CreateNewAddressEvent(-1, 0, -1, 0, 10001, publicKey1));
        gateway.createNewAddressEvent(new CreateNewAddressEvent(-1, 0, -1, 0, 10002, publicKey2));
        gateway.createNewAddressEvent(new CreateNewAddressEvent(-1, 0, -1, 0, 10003, publicKey3));
        gateway.createNewAddressEvent(new CreateNewAddressEvent(-1, 0, -1, 0, 1, publicKey));
    }

    public void close() {
        Closeable.closeQuietly(toOne);
        Closeable.closeQuietly(toTwo);
        Closeable.closeQuietly(toThree);
        Closeable.closeQuietly(one);
        Closeable.closeQuietly(two);
        Closeable.closeQuietly(three);
    }
}
