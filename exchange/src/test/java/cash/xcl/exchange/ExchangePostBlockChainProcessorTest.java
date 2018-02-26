package cash.xcl.exchange;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.NewOrderCommand;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.TextMethodTester;
import org.junit.Test;

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

    public static void main(String[] args) {
        NewOrderCommand nloc = new NewOrderCommand(2, 0, "clientId", "USDXCH", true, false, 100.0, 3.5);
        System.out.println(nloc);
    }
    @Test
    public void newLimitOrder() {
        test("exchange/new-limit-order");
    }
}