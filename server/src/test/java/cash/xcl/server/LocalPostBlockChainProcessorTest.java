package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.TextMethodTester;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LocalPostBlockChainProcessorTest {
    public static void test(String basename) {
        TextMethodTester<AllMessagesServer> tester = new TextMethodTester<>(basename + "/in.yaml",
                LocalPostBlockChainProcessor::new,
                AllMessagesServer.class, basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

    // *** openingBalanceEvent ***
    @Test
    public void openingBalanceEvent() {

        test("post-block-chain/opening-balance");
    }

    @Test
    public void openingBalanceEventFails() {

        test("post-block-chain/opening-balance-fails-bcz-already-set");
    }

    // *** transferValueCommand ***
    @Test
    public void transferValueCommand() {

        test("post-block-chain/transfer-value");
    }

    @Test
    public void transferValueCommandBczUnknownDestAddress() {

        test("post-block-chain/transfer-value-bcz-unknown-dest-address");
    }

    @Test
    public void transferValueCommandFailsBczNotEnoughBalance() {

        test("post-block-chain/transfer-value-fails-bcz-not-enough-balance");
    }

    @Test
    public void transferValueCommandFailsBczUnknownSrcAddress() {

        test("post-block-chain/transfer-value-fails-bcz-unknown-src-address");
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