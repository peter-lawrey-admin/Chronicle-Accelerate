package cash.xcl.exchange;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.util.XCLBase32;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.TextMethodTester;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ExchangePostBlockChainProcessorTest {
    public static void test(String basename) {
        TextMethodTester<AllMessagesServer> tester = new TextMethodTester<>(basename + "/in.yaml",
                (out) -> {
                    ExchangePostBlockChainProcessor usdxch = new ExchangePostBlockChainProcessor(
                            321321321321L,
                            XCLBase32.decode("2-USD"),
                            "USDXCH");
                    usdxch.allMessagesLookup(out);
                    return usdxch;
                },
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
    public static void main(String[] args) {
        NewOrderCommand nloc = new NewOrderCommand(2, 0, "clientId", "USDXCH", true, false, 100.0, 3.5);
        System.out.println(nloc);
    }
*/

    @Test
    public void newLimitOrder() {
        test("exchange/new-limit-order");
    }

    @Test
    public void newMarketOrder() {
        test("exchange/new-market-order");
    }

    @Test
    public void cancelOrders() {
        test("exchange/cancel-orders");
    }
}