package cash.xcl.server;

import cash.xcl.api.dto.CancelOrderCommand;
import cash.xcl.api.dto.NewLimitOrderCommand;
import cash.xcl.api.dto.NewMarketOrderCommand;

public class ExchangePostBlockChainProcessor extends LocalPostBlockChainProcessor {
    public ExchangePostBlockChainProcessor(long address) {
        super(address);
    }

    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        super.newLimitOrderCommand(newLimitOrderCommand);
    }

    @Override
    public void newMarketOrderCommand(NewMarketOrderCommand newMarketOrderCommand) {
        super.newMarketOrderCommand(newMarketOrderCommand);
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        super.cancelOrderCommand(cancelOrderCommand);
    }
}
