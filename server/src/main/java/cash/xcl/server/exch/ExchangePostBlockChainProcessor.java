package cash.xcl.server.exch;


import static cash.xcl.api.exch.Side.BUY;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.DepositValueEvent;
import cash.xcl.api.dto.WithdrawValueCommand;
import cash.xcl.api.dto.WithdrawValueEvent;
import cash.xcl.api.exch.CancelOrderCommand;
import cash.xcl.api.exch.CurrencyPair;
import cash.xcl.api.exch.ExecutionReport;
import cash.xcl.api.exch.ExecutionReportEvent;
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
    private final ExchangeAccounts exchangeAccounts;

    private final ExchangeMarket exchangeMarket;

    private final CurrencyPair currencyPair;

    private final int lotSize;

    private AllMessages finalRouter;


    public ExchangePostBlockChainProcessor(long address, CurrencyPair currencyPair, double tickSize, int lotSize,
                                           TimeProvider timeProvider,
                                           AllMessages finalRouter) {
        super(address);
        this.currencyPair = currencyPair;
        if (!currencyPair.getBaseCurrency().equals("XCL")) {
            throw new IllegalArgumentException("Base currency must be XCL for now");
        }
        this.lotSize = lotSize;
        this.finalRouter = finalRouter;
        this.exchangeAccounts = new ExchangeAccounts(currencyPair);
        this.exchangeMarket = new ExchangeMarket(tickSize, this::onTrade, this::onCancel);
    }

    // // only for testing purposes.
    public ExchangePostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        this(321321321321L, new CurrencyPair("XCL", "EUR"), 0.01D, 10, SystemTimeProvider.INSTANCE, new VanillaFinalRouter(12345));
        allMessagesLookup(allMessagesServer);
    }

    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        try {
            if (depositValueCommand.currency().equals(currencyPair.getQuoteCurrency())) {
                Account account = exchangeAccounts.getQuoteAccount(depositValueCommand.toAddress(), true);
                account.deposit(depositValueCommand.amount());
                finalRouter.depositValueEvent(new DepositValueEvent(address, timeProvider.currentTimeMicros(), depositValueCommand));
            } else {
                finalRouter.commandFailedEvent(new CommandFailedEvent(address, timeProvider.currentTimeMicros(), depositValueCommand,
                        "No such currency quoted on the exchange "));
            }
        } catch (Exception tfe) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, timeProvider.currentTimeMicros(), depositValueCommand, tfe.getMessage()));
        }
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        try {
            if (withdrawValueCommand.currency().equals(currencyPair.getQuoteCurrency())) {
                Account account = exchangeAccounts.getQuoteAccount(withdrawValueCommand.sourceAddress(), false);
                if (account != null) {
                    account.withdraw(withdrawValueCommand.amount());
                    finalRouter.withdrawValueEvent(new WithdrawValueEvent(address, timeProvider.currentTimeMicros(), withdrawValueCommand));
                } else {
                    finalRouter.commandFailedEvent(
                            new CommandFailedEvent(address, timeProvider.currentTimeMicros(), withdrawValueCommand,
                                    "No such account on the exchange"));
                }
            }
        } catch (Exception e) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, timeProvider.currentTimeMicros(), withdrawValueCommand, e.getMessage()));
        }
    }


    @Override
    public void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand) {
        long accountAddress = newLimitOrderCommand.sourceAddress();
        if (newLimitOrderCommand.getAction() == BUY) {
            Account quoteAccount = exchangeAccounts.getQuoteAccount(accountAddress, true);
            if (quoteAccount.lockMoney(newLimitOrderCommand.getQuantity() * newLimitOrderCommand.getMaxPrice() * lotSize)) {
                exchangeMarket.executeOrder(newLimitOrderCommand);
            } else {
                finalRouter.commandFailedEvent(
                        new CommandFailedEvent(address, timeProvider.currentTimeMicros(), newLimitOrderCommand, "Not enough funds"));
            }
        } else {
            Account baseAccount = exchangeAccounts.getBaseAccount(accountAddress, true);
            if (baseAccount.lockMoney(newLimitOrderCommand.getQuantity() * lotSize)) {
                exchangeMarket.executeOrder(newLimitOrderCommand);
            } else {
                finalRouter.commandFailedEvent(
                        new CommandFailedEvent(address, timeProvider.currentTimeMicros(), newLimitOrderCommand, "Not enough funds"));
            }
        }
    }

    private void onTrade(Order aggressor, Order initiator, long qty) {
        unlockMoney(aggressor, qty);
        unlockMoney(initiator, qty);
        ExecutionReport executionReport = new ExecutionReport(currencyPair, aggressor.getSide(), qty, initiator.getPrice(),
                aggressor.getOwnerAddress(), initiator.getOwnerAddress());
        finalRouter.executionReportEvent(new ExecutionReportEvent(address, timeProvider.currentTimeMicros(), executionReport));
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        exchangeMarket.cancelOrder(cancelOrderCommand.sourceAddress(), cancelOrderCommand.getOrderTime());
    }


    private void onCancel(Order order, OrderClosedEvent.REASON reason) {
        unlockMoney(order, order.getQuantityLeft());
        finalRouter.orderClosedEvent(new OrderClosedEvent(address, timeProvider.currentTimeMicros(), order.getOwnerAddress(),
                order.getOwnerOrderTime(), OrderClosedEvent.REASON.USER_REQUEST));
    }


    private void unlockMoney(Order order, long qty) {
        if (order.getSide() == BUY) {
            Account quoteAccount = exchangeAccounts.getQuoteAccount(order.getOwnerAddress(), false);
            assert quoteAccount != null;
            quoteAccount.unlockMoney(qty * order.getPrice() * lotSize);
        } else {
            Account baseAccount = exchangeAccounts.getBaseAccount(order.getOwnerAddress(), false);
            assert baseAccount != null;
            baseAccount.unlockMoney(qty * lotSize);
        }
    }


    @Override
    public void replyStarted() {
        exchangeMarket.setCurrentTime(timeProvider.currentTimeMillis());
    }

    @Override
    public void replyFinished() {
        exchangeMarket.removeExpired();
    }

}
