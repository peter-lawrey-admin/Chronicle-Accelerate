package cash.xcl.server.exch;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.WithdrawValueCommand;
import cash.xcl.api.exch.CancelOrderCommand;
import cash.xcl.api.exch.NewLimitOrderCommand;
import cash.xcl.api.exch.OrderClosedEvent;
import cash.xcl.server.LocalPostBlockChainProcessor;
import cash.xcl.server.VanillaFinalRouter;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;

/**
 * I assume that class is always called from the same thread, so no synchronization is necessary This component does no address validation
 * as it should be done somewhere higher on the call stack when the block of events was created.
 */
public class ExchangePostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final ExchangeAccount exchangeAccounts;

    private final ExchangeMarket exchangeMarket;

    private AllMessages finalRouter;

    public ExchangePostBlockChainProcessor(long address, String currency, double tickSize, TimeProvider timeProvider,
                                           AllMessages finalRouter) {
        super(address);
        this.finalRouter = finalRouter;
        this.exchangeAccounts = new ExchangeAccount(currency);
        this.exchangeMarket = new ExchangeMarket(tickSize);
    }

    // // only for testing purposes.
    public ExchangePostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        this(321321321321L, "EUR", 0.01D, SystemTimeProvider.INSTANCE, new VanillaFinalRouter(12345));
        allMessagesLookup(allMessagesServer);
    }

    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        try {
            exchangeAccounts.deposit(depositValueCommand.toAddress(), depositValueCommand.amount());
        } catch (Exception tfe) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, timeProvider.currentTimeMicros(), depositValueCommand, tfe.getMessage()));
        }
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        try {
            exchangeAccounts.withdraw(withdrawValueCommand.sourceAddress(), withdrawValueCommand.amount());
        } catch (Exception e) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, timeProvider.currentTimeMicros(), withdrawValueCommand, e.getMessage()));
        }
    }


    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        exchangeMarket.processOrder(newLimitOrderCommand);
    }


    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        exchangeMarket.cancelOrder(cancelOrderCommand.sourceAddress(), cancelOrderCommand.getOrderTime(), this::publishUserCancel);
    }

    private void publishUserCancel(Order order) {
        finalRouter.orderClosedEvent(new OrderClosedEvent(address, timeProvider.currentTimeMicros(), order.getOwnerAddress(),
                order.getOwnerOrderTime(), OrderClosedEvent.REASON.USER_REQUEST));
    }

    private void publishExpired(Order order) {
        finalRouter.orderClosedEvent(new OrderClosedEvent(address, timeProvider.currentTimeMicros(), order.getOwnerAddress(),
                order.getOwnerOrderTime(), OrderClosedEvent.REASON.TIME_OUT));
    }

    @Override
    public void replyStarted() {
        exchangeMarket.setCurrentTime(timeProvider.currentTimeMillis());
    }

    @Override
    public void replyFinished() {
        exchangeMarket.removeExpired(this::publishExpired);
    }

}
