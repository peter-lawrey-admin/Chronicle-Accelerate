package cash.xcl.server.exch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Test;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.exch.CurrencyPair;
import cash.xcl.api.exch.DepositValueCommand;
import cash.xcl.api.exch.WithdrawValueCommand;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.wire.TextMethodTester;

public class ExchangePostBlockChainProcessorTest {
    public static void test(String basename, Function<AllMessagesServer, Object> creator) {
        TextMethodTester<AllMessagesServer> tester = new TextMethodTester<>(basename + "/in.yaml", creator, AllMessagesServer.class,
                basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

    @Test
    public void depositWithdrawYaml() {
        test("post-block-chain/exch/deposit-withdraw", (out) -> buildProcessor(out));
    }

    static ExchangePostBlockChainProcessor buildProcessor(AllMessagesServer router) {
        long serverAddress = 12345;
        SetTimeProvider timeProvider = new SetTimeProvider();
        return new ExchangePostBlockChainProcessor(serverAddress,
                new CurrencyPair("XCL", "USD"), timeProvider, router);
    }


    public static void main(String[] args) {
        long crtTime = 100000000;
        long serverAddress = 12345;
        long bankAddress = 12345;
        int timeIncrement = 1001;
        long userAddress = 1;

        try (ExchangePostBlockChainProcessor exc = buildProcessor(Mocker.logging(AllMessagesServer.class, "", System.out))) {
            DepositValueCommand depositValueCommand = new DepositValueCommand(bankAddress, crtTime - (timeIncrement / 5), userAddress,
                    1000D, "USD");
            exc.depositValueCommand(depositValueCommand);

            CurrentBalanceQuery cbq = new CurrentBalanceQuery(7273, 15, userAddress);
            exc.currentBalanceQuery(cbq);

            WithdrawValueCommand withdrawValueCommand = new WithdrawValueCommand(1, 25, 1, 100, "USD", "Cash", "Checking", "Bank");
            exc.withdrawValueCommand(withdrawValueCommand);
        }

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