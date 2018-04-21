package cash.xcl.api.exch;

import cash.xcl.api.dto.MessageTypes;
import cash.xcl.api.dto.TransferValueCommand;

public class TransferFromExchangeCommand extends TransferValueCommand {

    public TransferFromExchangeCommand() {
        super();
    }

    public TransferFromExchangeCommand(long sourceAddress, long eventTime, double amount, String currency, String reference) {
        super(sourceAddress, eventTime, sourceAddress, amount, currency, reference);
    }

    @Override
    public int intMessageType() {
        return MessageTypes.TRANSFER_FROM_EXCHANGE_COMMAND;
    }

}
