package cash.xcl.server.mock;

import cash.xcl.api.ClientIn;
import cash.xcl.api.dto.CreateNewAddressCommand;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Mocker;
import org.junit.Test;

import java.io.StringWriter;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class MockGatewayTest {

    @Test
    public void createNewAddressCommand() throws IllegalAccessException {
        // initialise the random
        ThreadLocalRandom.current().nextInt();

        // overwrite the seed.
        Jvm.getField(Thread.class, "threadLocalRandomSeed")
                .setLong(Thread.currentThread(), 1);

        StringWriter out = new StringWriter();
        ClientIn clientIn = Mocker.logging(ClientIn.class, "", out);
        MockGateway mg = new MockGateway(clientIn);

        Bytes<byte[]> publicKey = Bytes.wrapForRead(new byte[32]);
        mg.createNewAddressCommand(new CreateNewAddressCommand(1, 0, publicKey, "IE-A"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(1, 0, publicKey, "IE-CO"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(1, 0, publicKey, "IE-D"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(1, 0, publicKey, "IE-DL"));

        assertEquals("commandFailedEvent[!CommandFailedEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 1,\n" +
                "  origEventTime: 0,\n" +
                "  origProtocol: 1,\n" +
                "  origMessageType: 32,\n" +
                "  reason: Unknown region code IEA\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 1,\n" +
                "  origEventTime: 0,\n" +
                "  address: iecooqflzupcn,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 1,\n" +
                "  origEventTime: 0,\n" +
                "  address: ied2hsmj3yz5n,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 1,\n" +
                "  origEventTime: 0,\n" +
                "  address: iedlspl6chtda,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n", out.toString()
                .replaceAll("\r", ""));
    }
}