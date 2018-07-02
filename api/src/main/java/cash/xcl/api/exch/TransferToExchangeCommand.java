package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.TransferValueCommand;

public class TransferToExchangeCommand extends TransferValueCommand {

    public TransferToExchangeCommand() {
        super();
    }

    public TransferToExchangeCommand(long sourceAddress, long eventTime, double amount, String currency, String reference) {
        super(sourceAddress, eventTime, sourceAddress, amount, currency, reference);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.TRANSFER_TO_EXCHANGE_COMMAND;
    }

}
