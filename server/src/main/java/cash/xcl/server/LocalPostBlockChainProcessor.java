package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.PostBlockChainProcessor;
import cash.xcl.api.dto.*;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.server.accounts.AccountService;
import cash.xcl.server.accounts.BalanceByCurrency;
import cash.xcl.server.accounts.VanillaAccountService;
import cash.xcl.util.XCLBase32;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.annotation.NotNull;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;



public class LocalPostBlockChainProcessor extends AbstractAllMessages implements AllMessagesServer, PostBlockChainProcessor {


    private AccountService accountService; // todo review

    private TransferValueEvent tve = new TransferValueEvent();
//    private DepositValueEvent dve = new DepositValueEvent();
//    private WithdrawValueEvent wve = new WithdrawValueEvent();
    private CurrentBalanceResponse cbr = new CurrentBalanceResponse();
    private ExchangeRateResponse err = new ExchangeRateResponse();

    private int numberOfTransferEventsSent = 0;
    private int numberOfOpeningBalanceEventsSent = 0;
//    private int numberOfDepositEventsSent = 0;
//    private int numberOfWithdrawalEventsSent = 0;


    protected TimeProvider timeProvider = SystemTimeProvider.INSTANCE;


    public LocalPostBlockChainProcessor(long address) {
        super(address);
        this.accountService = new VanillaAccountService();
    }

    // Used for testing purposes
    LocalPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(XCLBase32.decode("test.server"));
        allMessagesLookup(allMessagesServer);
        this.accountService = new VanillaAccountService();
    }


    // For testing purposes only
    public void time(long time) {
        if (timeProvider instanceof SystemTimeProvider) {
            timeProvider = new SetTimeProvider();
        }
        ((SetTimeProvider) timeProvider).currentTimeMicros(time);
    }


    @Override
    public void openingBalanceEvent(@NotNull final OpeningBalanceEvent openingBalanceEvent) {

        //System.out.println("received " + openingBalanceEvent);

        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = openingBalanceEvent.sourceAddress();
        try {
            accountService.setOpeningBalancesForAccount(openingBalanceEvent);
            numberOfOpeningBalanceEventsSent++;
            if( numberOfOpeningBalanceEventsSent % 50 == 0)
                System.out.println( "PostProcessor - numberOfOpeningBalanceEventsSent = " + numberOfOpeningBalanceEventsSent);
        } catch (Exception e) {
            e.printStackTrace();
            Jvm.warn().on(getClass(), e.toString());
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, openingBalanceEvent, e.toString());
            AllMessages allMessages = to(sourceAddress);
            allMessages.commandFailedEvent(cfe);
        }
    }

    @Override
    public void transferValueCommand(@NotNull final TransferValueCommand transferValueCommand) {
//        System.out.println(transferValueCommand.sigAndMsg().readRemaining());
        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = transferValueCommand.sourceAddress();
        //long toAddress = transferValueCommand.toAddress();
        try {
            //accountService.print();
            accountService.transfer(transferValueCommand);
            //accountService.print();
            tve.init(this.address, eventTime, transferValueCommand);

            // source account Client:
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.transferValueEvent(tve);

            // do we need to send an event to the client for the destination account?
            // destination account Client:
            // AllMessages toAccount = to(toAddress);
            // toAccount.transferValueEvent(tve);

            numberOfTransferEventsSent++;
            if (numberOfTransferEventsSent % 100000 == 0)
                System.out.println("PostProcessor - number of transfers = " + numberOfTransferEventsSent);

        } catch (Exception e) {
            e.printStackTrace();
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, transferValueCommand, e.toString());
            AllMessages allMessages = to(sourceAddress);
            allMessages.commandFailedEvent(cfe);
        }
    }


    public void notifyEndOfRound() {

    }

    // only for testing purposes
    public void printBalances() {
        if( this.accountService != null ) {
            this.accountService.print();
        }
    }


//    @Override
//    public void depositValueCommand(@NotNull final DepositValueCommand depositValueCommand) {
//        //System.out.println(depositValueCommand.sigAndMsg().readRemaining());
//        long eventTime = timeProvider.currentTimeMicros();
//        long sourceAddress = depositValueCommand.sourceAddress();
//        //long toAddress = depositValueCommand.toAddress();
//        try {
//            transferValueCommand(depositValueCommand);
//            //accountService.print();
//            accountService.transfer(depositValueCommand);
//            //accountService.print();
//            this.dve.init(this.address, eventTime, depositValueCommand);
//
//            // source account Client:
//            AllMessages messageWriter = to(sourceAddress);
//            messageWriter.depositValueEvent(this.dve);
//
//            // do we need to send an event to the client for the destination account?
//            // destination account Client:
//            // AllMessages messageWriter = to(toAddress);
//            // messageWriter.depositValueEvent(tve);
//
//            numberOfDepositEventsSent++;
//            System.out.println("PostProcessor - number of deposits = " + numberOfDepositEventsSent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, depositValueCommand, e.toString());
//            AllMessages messageWriter = to(sourceAddress);
//            messageWriter.commandFailedEvent(cfe);
//        }
//    }
//
//
//    @Override
//    public void withdrawValueCommand(@NotNull final WithdrawValueCommand withdrawValueCommand) {
//        //System.out.println(depositValueCommand.sigAndMsg().readRemaining());
//        long eventTime = timeProvider.currentTimeMicros();
//        long sourceAddress = withdrawValueCommand.sourceAddress();
//        //long toAddress = depositValueCommand.toAddress();
//        try {
//            transferValueCommand(withdrawValueCommand);
//            //accountService.print();
//            accountService.transfer(withdrawValueCommand);
//            //accountService.print();
//            this.wve.init(this.address, eventTime, withdrawValueCommand);
//
//            // source account Client:
//            AllMessages messageWriter = to(sourceAddress);
//            messageWriter.withdrawValueEvent(this.wve);
//
//            // do we need to send an event to the client for the destination account?
//            // destination account Client:
//            // AllMessages messageWriter = to(toAddress);
//            // messageWriter.withdrawValueEvent(wve);
//
//            numberOfWithdrawalEventsSent++;
//            System.out.println("PostProcessor - number of withdrawals = " + numberOfWithdrawalEventsSent);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, withdrawValueCommand, e.toString());
//            AllMessages messageWriter = to(sourceAddress);
//            messageWriter.commandFailedEvent(cfe);
//        }
//    }

    @Override
    public void currentBalanceQuery(@NotNull final CurrentBalanceQuery currentBalanceQuery) {
        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = currentBalanceQuery.sourceAddress();
        try {
            long accountAddress = currentBalanceQuery.address();
            BalanceByCurrency balanceByCurrency = accountService.balances(accountAddress);
            if( balanceByCurrency == null)
                throw new Exception("AccountService does not have a balance for account = " + accountAddress);
            this.cbr.init(this.address, eventTime, accountAddress, balanceByCurrency.balances());

            AllMessages messageWriter = to(sourceAddress);
            messageWriter.currentBalanceResponse(this.cbr);
        } catch (Exception e) {
            //e.printStackTrace();
            QueryFailedResponse qfr = new QueryFailedResponse(address, eventTime, currentBalanceQuery, e.toString());
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.queryFailedResponse(qfr);
        }
    }

    // exchangeRateQuery should go the Exchange service/node???
    @Override
    public void exchangeRateQuery(@NotNull final ExchangeRateQuery exchangeRateQuery) {
        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = exchangeRateQuery.sourceAddress();
        try {
            // TODO get rate for exchangeRateQuery.symbol1() vs exchangeRateQuery.symbol2()
            String symbol1symbol2 = exchangeRateQuery.symbol1() + exchangeRateQuery.symbol2();
            double midPrice = 0d;
            double spreadPrice = 0d;
            this.err.init(this.address, eventTime, symbol1symbol2, midPrice, spreadPrice);

            AllMessages messageWriter = to(sourceAddress);
            messageWriter.exchangeRateResponse(this.err);
        } catch (Exception e) {
            e.printStackTrace();
            QueryFailedResponse qfr = new QueryFailedResponse(address, eventTime, exchangeRateQuery, e.toString());
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.queryFailedResponse(qfr);
        }
    }


}