package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.server.exch.ExchangePostBlockChainProcessor;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.TextMethodTester;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ExchangePostBlockChainProcessorTest {
    public static void test(String basename) {
        TextMethodTester<AllMessagesServer> tester = new TextMethodTester<>(basename + "/in.yaml",
                ExchangePostBlockChainProcessor::new,
                AllMessagesServer.class, basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

/*

    @Test
    public void createNewAddressCommand() {
        test("post-block-chain/create-new-address");
    }

    @Test
    public void createNewAddressCommandFails() {
        test("post-block-chain/create-new-address-fails");
    }
*/
}