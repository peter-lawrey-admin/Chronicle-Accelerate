package cash.xcl.server.exch;


import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.CurrentBalanceResponse;
import cash.xcl.api.dto.TransferValueEvent;
import cash.xcl.api.exch.*;
import cash.xcl.server.LocalPostBlockChainProcessor;
import net.openhft.chronicle.core.time.TimeProvider;

import java.util.Map;

import static cash.xcl.api.dto.Validators.notNull;
import static cash.xcl.api.exch.Side.BUY;

/**
 * I assume that class is always called from the same thread, so no synchronization is necessary This component does no address validation
 * as it should be done somewhere higher on the call stack when the block of events was created.
 */
public class ExchangePostBlockChainProcessor extends LocalPostBlockChainProcessor implements ExchangeCommands {
    private final ExchangeAccounts exchangeAccounts;

    private final ExchangeMarket exchangeMarket;

    private final CurrencyPair currencyPair;

    private final int lotSize;

    private AllMessages finalRouter;


    public ExchangePostBlockChainProcessor(long address, CurrencyPair currencyPair, double tickSize, int lotSize,
            TimeProvider timeProvider,
            AllMessages finalRouter) {
        super(address);
        this.currencyPair = notNull(currencyPair);
        if (!currencyPair.getBaseCurrency().equals("XCL")) {
            throw new IllegalArgumentException("Base currency must be XCL for now");
        }
        this.lotSize = lotSize;
        this.timeProvider = notNull(timeProvider);
        this.finalRouter = notNull(finalRouter);
        this.exchangeAccounts = new ExchangeAccounts(currencyPair);
        this.exchangeMarket = new ExchangeMarket(tickSize, this::onTrade, this::onCancel);
    }

    public ExchangePostBlockChainProcessor(long address, CurrencyPair currencyPair, TimeProvider timeProvider, AllMessages finalRouter) {
        this(address, currencyPair, 0.01, 1, timeProvider, finalRouter);
    }


    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        try {
            String currency = depositValueCommand.currencyStr();
            String quoteCurrency = currencyPair.getQuoteCurrency();
            if (currency.equalsIgnoreCase(quoteCurrency)) {
                Account account = exchangeAccounts.getQuoteAccount(depositValueCommand.toAddress(), true);
                account.deposit(depositValueCommand.amount());
                finalRouter.depositValueEvent(new DepositValueEvent(address, microsTimeStamp(), depositValueCommand));
            } else {
                finalRouter.commandFailedEvent(new CommandFailedEvent(address, microsTimeStamp(), depositValueCommand,
                        "No such currency quoted on the exchange "));
            }
        } catch (Exception tfe) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, microsTimeStamp(), depositValueCommand, tfe.getMessage()));
        }
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        try {
            String currency = withdrawValueCommand.currencyStr();
            String quoteCurrency = currencyPair.getQuoteCurrency();
            if (currency.equalsIgnoreCase(quoteCurrency)) {
                Account account = exchangeAccounts.getQuoteAccount(withdrawValueCommand.sourceAddress(), false);
                if (account != null) {
                    account.withdraw(withdrawValueCommand.amount());
                    finalRouter.withdrawValueEvent(new WithdrawValueEvent(address, microsTimeStamp(), withdrawValueCommand));
                } else {
                    finalRouter.commandFailedEvent(
                            new CommandFailedEvent(address, microsTimeStamp(), withdrawValueCommand,
                                    "No such account on the exchange"));
                }
            }
        } catch (Exception e) {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, microsTimeStamp(), withdrawValueCommand, e.getMessage()));
        }
    }

    @Override
    public void newOrderCommand(NewOrderCommand newLimitOrderCommand) {
        long accountAddress = newLimitOrderCommand.sourceAddress();
        if (newLimitOrderCommand.getAction() == BUY) {
            Account quoteAccount = exchangeAccounts.getQuoteAccount(accountAddress, true);
            if (quoteAccount.lockMoney(newLimitOrderCommand.getQuantity() * newLimitOrderCommand.getMaxPrice() * lotSize)) {
                exchangeMarket.executeOrder(newLimitOrderCommand);
            } else {
                finalRouter.commandFailedEvent(
                        new CommandFailedEvent(address, microsTimeStamp(), newLimitOrderCommand, "Not enough funds"));
            }
        } else {
            Account baseAccount = exchangeAccounts.getBaseAccount(accountAddress, true);
            if (baseAccount.lockMoney(newLimitOrderCommand.getQuantity() * lotSize)) {
                exchangeMarket.executeOrder(newLimitOrderCommand);
            } else {
                finalRouter.commandFailedEvent(
                        new CommandFailedEvent(address, microsTimeStamp(), newLimitOrderCommand, "Not enough funds"));
            }
        }
    }

    @Override
    public void transferToExchangeCommand(TransferToExchangeCommand transferCommand) {
        long accountAddress = transferCommand.sourceAddress();
        String currency = transferCommand.currencyStr();
        if (currency.equals(currencyPair.getBaseCurrency())) {
            Account account = exchangeAccounts.getBaseAccount(accountAddress, true);
            account.deposit(transferCommand.amount());
            finalRouter.transferValueEvent(new TransferValueEvent(address, microsTimeStamp(), transferCommand));
        } else if (currency.equals(currencyPair.getQuoteCurrency())) {
            Account account = exchangeAccounts.getQuoteAccount(accountAddress, true);
            account.deposit(transferCommand.amount());
            finalRouter.transferValueEvent(new TransferValueEvent(address, microsTimeStamp(), transferCommand));
        } else {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, microsTimeStamp(), transferCommand, "Unsupported currency " + currency));
        }
    }

    private long microsTimeStamp() {
        return timeProvider.currentTimeMicros();
    }

    @Override
    public void transferFromExchangeCommand(TransferFromExchangeCommand transferCommand) {
        long accountAddress = transferCommand.sourceAddress();
        String currency = transferCommand.currencyStr();
        if (currency.equals(currencyPair.getBaseCurrency())) {
            Account account = exchangeAccounts.getBaseAccount(accountAddress, false);
            if (account != null) {
                try {
                    account.withdraw(transferCommand.amount());
                    finalRouter.transferValueEvent(new TransferValueEvent(address, microsTimeStamp(), transferCommand));
                } catch (TransactionFailedException e) {
                    finalRouter.commandFailedEvent(
                            new CommandFailedEvent(address, microsTimeStamp(), transferCommand, e.getMessage()));
                }
            } else {
                finalRouter.commandFailedEvent(new CommandFailedEvent(address, microsTimeStamp(), transferCommand,
                        "Unknown account " + accountAddress));
            }
        } else if (currency.equals(currencyPair.getQuoteCurrency())) {
            Account account = exchangeAccounts.getQuoteAccount(accountAddress, false);
            if (account != null) {
                try {
                    account.withdraw(transferCommand.amount());
                    finalRouter.transferValueEvent(new TransferValueEvent(address, microsTimeStamp(), transferCommand));
                } catch (TransactionFailedException e) {
                    finalRouter.commandFailedEvent(
                            new CommandFailedEvent(address, microsTimeStamp(), transferCommand, e.getMessage()));
                }
            } else {
                finalRouter.commandFailedEvent(new CommandFailedEvent(address, microsTimeStamp(), transferCommand,
                        "Unknown account " + accountAddress));
            }
        } else {
            finalRouter.commandFailedEvent(
                    new CommandFailedEvent(address, microsTimeStamp(), transferCommand, "Unsupported currency " + currency));
        }
    }

    @Override
    public void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery) {
        long accountAddress = currentBalanceQuery.address();
        Map<String, Double> balances = exchangeAccounts.getBalance(accountAddress);
        if (balances != null) {
            finalRouter.currentBalanceResponse(new CurrentBalanceResponse(address, microsTimeStamp(), accountAddress, balances));
        }
    }

    private void onTrade(Order aggressor, Order initiator, double qty) {
        unlockMoney(aggressor, qty);
        unlockMoney(initiator, qty);
        ExecutionReport executionReport = new ExecutionReport(currencyPair, aggressor.getSide(), qty, initiator.getPrice(),
                aggressor.getOwnerAddress(), initiator.getOwnerAddress());
        finalRouter.executionReportEvent(new ExecutionReportEvent(address, microsTimeStamp(), executionReport));
    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        exchangeMarket.cancelOrder(cancelOrderCommand.sourceAddress(), cancelOrderCommand.getOrderTime());
    }


    private void onCancel(Order order, OrderClosedEvent.REASON reason) {
        unlockMoney(order, order.getQuantityLeft());
        finalRouter.orderClosedEvent(new OrderClosedEvent(address, microsTimeStamp(), order.getOwnerAddress(),
                order.getOwnerOrderTime(), OrderClosedEvent.REASON.USER_REQUEST));
    }


    private void unlockMoney(Order order, double qty) {
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
    public void replayStarted() {
        exchangeMarket.setCurrentTime(timeProvider.currentTimeMillis());
    }

    @Override
    public void replayFinished() {
        exchangeMarket.removeExpired();
    }

    @Override
    public void close() {
        exchangeMarket.close();
    }

}
