package cash.xcl.server;

import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.VanillaBytes;

import java.io.IOException;

public class RegionalServerMain {
    static XCLServer server;

    public static void main(String[] args) throws IOException {
        VanillaBytes<Void> secretKey = Bytes.allocateDirect(32);
        secretKey.writeSkip(32);
        server = new XCLServer("regional", 12345, 0, secretKey, new RegionalServer());


    }
}
