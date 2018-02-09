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
        mg.createNewAddressCommand(new CreateNewAddressCommand(0, 0, publicKey, "IE-A"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(0, 0, publicKey, "IE-CO"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(0, 0, publicKey, "IE-D"));
        mg.createNewAddressCommand(new CreateNewAddressCommand(0, 0, publicKey, "IE-DL"));

        assertEquals("commandFailedEvent[!cash.xcl.api.dto.CommandFailedEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 0,\n" +
                "  origEventTime: 0,\n" +
                "  origMessageType: 32,\n" +
                "  reason: Unknown region code iea\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!cash.xcl.api.dto.CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 0,\n" +
                "  origEventTime: 0,\n" +
                "  address: -7811493390981337397,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!cash.xcl.api.dto.CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 0,\n" +
                "  origEventTime: 0,\n" +
                "  address: -7810885632407925157,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n" +
                "createNewAddressEvent[!cash.xcl.api.dto.CreateNewAddressEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  origSourceAddress: 0,\n" +
                "  origEventTime: 0,\n" +
                "  address: -7810898871858203435,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=\n" +
                "}\n" +
                "]\n", out.toString()
                .replaceAll("\r", ""));
    }
}