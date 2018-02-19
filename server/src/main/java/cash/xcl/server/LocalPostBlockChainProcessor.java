package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.util.AbstractAllMessages;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;

public class LocalPostBlockChainProcessor extends AbstractAllMessages implements AllMessagesServer {
    public LocalPostBlockChainProcessor(long address) {
        super(address);
    }

    protected TimeProvider timeProvider = SystemTimeProvider.INSTANCE;

    // For testing purposes only
    public LocalPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(123123123123L);
        allMessagesLookup(allMessagesServer);
    }

    // For testing purposes only
    public void time(long time) {
        if (timeProvider instanceof SystemTimeProvider)
            timeProvider = new SetTimeProvider();
        ((SetTimeProvider) timeProvider).currentTimeMicros(time);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        super.transferValueCommand(transferValueCommand);
    }
}
