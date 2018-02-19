package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.util.AbstractAllMessages;

public class LocalPostBlockChainProcessor extends AbstractAllMessages implements AllMessagesServer {
    public LocalPostBlockChainProcessor(long address) {
        super(address);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        super.transferValueCommand(transferValueCommand);
    }
}
