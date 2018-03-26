package cash.xcl.api;

import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.api.util.XCLBase32;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class XCLClientServerTest {
    @Test
    public void test() throws IOException {
        Bytes privateKey = Bytes.elasticByteBuffer(32);
        privateKey.zeroOut(0, 32);
        privateKey.writeSkip(32);

        Bytes publicKey = Bytes.elasticByteBuffer(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.elasticByteBuffer(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);

        StringWriter out = new StringWriter();
        AllMessagesServer logging = Mocker.logging(AllMessagesServer.class, "", out);
        XCLServer server = new XCLServer("test", 9900, 1, publicKey, secretKey, logging);

        List<InetSocketAddress> addresses = Arrays.asList(new InetSocketAddress("localhost", 9900));
        AllMessages logging2 = Mocker.logging(AllMessages.class, "", out);
        ClientOut client = new XCLClient("test-client", addresses, 2, secretKey, logging2);
        server.register(2, publicKey);

        CreateNewAddressCommand cnac = new CreateNewAddressCommand(2, 1L, publicKey, "usny");
        assertEquals("usny", XCLBase32.encodeInt(cnac.region()));
        assertEquals("usny", XCLBase32.encodeInt2(cnac.region()));
        assertEquals("USNY", XCLBase32.encodeIntUpper(cnac.region()));
        client.createNewAddressCommand(
                cnac);
        for (int i = 0; i <= 20; i++) {
            assertTrue(i < 20);
            Jvm.pause(Jvm.isDebug() ? 2000 : 25);
            if (out.toString().contains("createNewAddressCommand")) {
                break;
            }
            System.out.println(out);
        }
        assertEquals(
                "allMessagesLookup[cash.xcl.api.tcp.XCLServer@xxxxxxxx]\n" + "createNewAddressCommand[!CreateNewAddressCommand {\n"
                        + "  sourceAddress: 2,\n" + "  eventTime: 1,\n" + "  publicKey: !!binary O2onvM62pC1io6jQKm8Nc2UyFXcd4kOmOsBIoYtZ2ik=,\n"
                        + "  region: USNY,\n" + "  newAddressSeed: 0\n" + "}\n" + "]\n",
                out.toString().replaceAll("\r", "").replaceAll("XCLServer@\\w+", "XCLServer@xxxxxxxx"));

        client.close();
        server.close();
    }
}
