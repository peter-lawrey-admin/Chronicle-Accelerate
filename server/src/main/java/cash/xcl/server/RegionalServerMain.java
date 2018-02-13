package cash.xcl.server;

import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.chain.Chainer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.VanillaBytes;

import java.io.IOException;

public class RegionalServerMain {
    static XCLServer server;

    public static void main(String[] args) throws IOException {
        VanillaBytes<Void> secretKey = Bytes.allocateDirect(32);
        secretKey.writeSkip(32);
        RegionalServer regionalServer = new RegionalServer();
        Chainer chainer = new Chainer(10, new long[0], tbe -> tbe.replay(regionalServer));
        server = new XCLServer("regional", 12345, 0, secretKey, chainer);
        regionalServer.allMessagesLookup(server);
    }
}
