package cash.xcl.server;

import cash.xcl.api.dto.ApplicationMessageEvent;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.VanillaBytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;

public class ThreeServerContainer implements Closeable {

    private final XCLServer one, two, three;

    public ThreeServerContainer() throws IOException {
        VanillaBytes<Void> secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        secretKey.writeSkip(Ed25519.SECRET_KEY_LENGTH);

        RegionalGateway gateway1 = new RegionalGateway(1001);
        one = new XCLServer("one", 1001, 1001, secretKey, gateway1);
        RegionalGateway gateway2 = new RegionalGateway(1002);
        two = new XCLServer("two", 1002, 1002, secretKey, gateway2);
        RegionalGateway gateway3 = new RegionalGateway(1003);
        three = new XCLServer("two", 1003, 1003, secretKey, gateway3);

        one.addTCPConnection(1002,
                new XCLClient("two", "localhost", 1002, 1001, secretKey, gateway1));
        one.addTCPConnection(1003,
                new XCLClient("three", "localhost", 1003, 1001, secretKey, gateway1));

        two.addTCPConnection(1001,
                new XCLClient("one", "localhost", 1001, 1002, secretKey, gateway2));
        two.addTCPConnection(1003,
                new XCLClient("three", "localhost", 1003, 1002, secretKey, gateway2));

        three.addTCPConnection(1002,
                new XCLClient("two", "localhost", 1002, 1003, secretKey, gateway3));
        three.addTCPConnection(1001,
                new XCLClient("one", "localhost", 1001, 1003, secretKey, gateway3));
    }

    public static void main(String[] args) throws IOException {
        ThreeServerContainer tsc = new ThreeServerContainer();
        tsc.one.to(1002)
                .applicationMessageEvent(
                        new ApplicationMessageEvent());
        Jvm.pause(1000);
        tsc.close();
    }

    @Override
    public void close() {
        Closeable.closeQuietly(one);
        Closeable.closeQuietly(two);
        Closeable.closeQuietly(three);
    }
}
