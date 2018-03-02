package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.PostBlockChainProcessor;
import cash.xcl.api.dto.*;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.server.accounts.AccountService;
import cash.xcl.server.accounts.VanillaAccountService;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;


public class LocalPostBlockChainProcessor extends AbstractAllMessages implements AllMessagesServer, PostBlockChainProcessor {


    private AccountService accountService; // todo review
    private int numberOfTransferEventsSent = 0;
    private int numberOfOpeningBalanceEventsSent = 0;

    public LocalPostBlockChainProcessor(long address) {
        super(address);
        this.accountService = new VanillaAccountService();
    }


    protected TimeProvider timeProvider = SystemTimeProvider.INSTANCE;

    // For testing purposes only
    public LocalPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(123123123123L);
        allMessagesLookup(allMessagesServer);
    }

    // For testing purposes only
    public void time(long time) {
        if (timeProvider instanceof SystemTimeProvider) {
            timeProvider = new SetTimeProvider();
        }
        ((SetTimeProvider) timeProvider).currentTimeMicros(time);
    }


    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {

        //System.out.println("received " + openingBalanceEvent);

        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = openingBalanceEvent.sourceAddress();
        try {
            accountService.setOpeningBalancesForAccount(openingBalanceEvent);
            numberOfOpeningBalanceEventsSent++;
            System.out.println( "PostProcessor - numberOfOpeningBalanceEventsSent = " + numberOfOpeningBalanceEventsSent);
        } catch (Exception e) {
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, openingBalanceEvent, e.toString());
            AllMessages allMessages = to(sourceAddress);
            allMessages.commandFailedEvent(cfe);
        }
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = transferValueCommand.sourceAddress();
        //long toAddress = transferValueCommand.toAddress();
        try {
            //accountService.print();
            accountService.transfer(transferValueCommand);
            //accountService.print();
            TransferValueEvent tve = new TransferValueEvent(this.address, eventTime, transferValueCommand);

            // source account Client:
            AllMessages sourceAccount = to(sourceAddress);
            sourceAccount.transferValueEvent(tve);

            // do we need to send an event to the client for the destination account?
            // destination account Client:
            // AllMessages toAccount = to(toAddress);
            // toAccount.transferValueEvent(tve);

            numberOfTransferEventsSent++;
            if( numberOfTransferEventsSent % 1000 == 0 )
                System.out.println("PostProcessor - number of transfers = " + numberOfTransferEventsSent);
        } catch (Exception e) {
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

}