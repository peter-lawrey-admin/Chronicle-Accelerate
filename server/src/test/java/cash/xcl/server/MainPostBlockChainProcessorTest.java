package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.TextMethodTester;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MainPostBlockChainProcessorTest {
    public static void test(String basename) {
        TextMethodTester<AllMessagesServer> tester = new TextMethodTester<>(basename + "/in.yaml",
                MainPostBlockChainProcessor::new,
                AllMessagesServer.class, basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

    public static void main(String[] args) {
        AddressService as = new AddressService();
        long usny = as.generateAddress("usny");
        System.out.println(usny);
    }

    @Test
    public void createNewAddressCommand() {

        test("post-block-chain/create-new-address");
    }

    @Test
    public void createNewAddressCommandFails() {

        test("post-block-chain/create-new-address-fails");
    }
}