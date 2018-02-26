package cash.xcl.exchange;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CancelOrderCommand;
import cash.xcl.api.dto.ExecutionReportEvent;
import cash.xcl.api.dto.NewOrderCommand;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.exchange.model.Order;
import cash.xcl.exchange.model.OrderBook;
import cash.xcl.server.LocalPostBlockChainProcessor;

public class ExchangePostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final AllMessages reportListener;
    private OrderBook buyOrderBook = new OrderBook(true);
    private OrderBook sellOrderBook = new OrderBook(false);
    private long startOfRoundTime = 0;

    public ExchangePostBlockChainProcessor(long address) {
        this(address, null);
    }

    // only for testing purposes.
    public ExchangePostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        this(321321321321L, allMessagesServer);
    }

    private ExchangePostBlockChainProcessor(long address, AllMessagesServer allMessagesServer) {
        super(address);
        allMessagesLookup(allMessagesServer);
        reportListener = new AbstractAllMessages(address) {
            @Override
            public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
                executionReportEvent.sourceAddress(address);
                executionReportEvent.eventTime(timeProvider.currentTimeMicros());
                ExchangePostBlockChainProcessor.this.to(executionReportEvent.clientAddress())
                        .executionReportEvent(executionReportEvent);
            }
        };
    }

    // assumes a and b are non-negative.
    static int compare(double a, double b, double ratio) {
        double err = (a + b) * ratio;
        return a > b + err ? +1
                : b > a + err ? -1
                : 0;
    }

    @Override
    public void newOrderCommand(NewOrderCommand newOrderCommand) {
        // todo check this is not a duplicate order.
        // todo check the address has enough balance and remove the balance.
        long startOfRoundTime = this.startOfRoundTime++; // uniqueness by order added.
        (newOrderCommand.isBuy() ? buyOrderBook : sellOrderBook)
                .addOrder(startOfRoundTime, newOrderCommand);
    }

    @Override
    public void notifyStartOfRound(long startOfRoundTime) {
        this.startOfRoundTime = startOfRoundTime;
    }

    @Override
    public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
        super.executionReportEvent(executionReportEvent);
    }

    @Override
    public void notifyEndOfRound() {
        try {
            buyOrderBook.startEOR();
            sellOrderBook.startEOR();

            double buyQuantity = 0, sellQuantity = 0;
            double buyPrice = Double.NaN, sellPrice = Double.NaN;
            while (buyOrderBook.hasMoreOrders() && sellOrderBook.hasMoreOrders()) {
                Order buy = buyOrderBook.nextOrder();
                Order sell = sellOrderBook.nextOrder();
                if (buy.price() < sell.price()) // false is either is NaN
                    break;
                int cmp = compare(buyQuantity, sellQuantity, 1e-6);
                if (cmp >= 0) {
                    sellQuantity += sell.quantity();
                    sellPrice = sell.price();
                    sellOrderBook.advanceNext();
                }
                if (cmp <= 0) {
                    buyQuantity += buy.quantity();
                    buyPrice += buy.price();
                    buyOrderBook.advanceNext();
                }
            }
            double midPrice, matchedQuantity;
            if (buyPrice > 0) {
                if (sellPrice > 0) {
                    midPrice = (buyPrice + sellPrice) / 2;
                    matchedQuantity = Math.min(buyQuantity, sellQuantity);
                } else {
                    midPrice = buyPrice;
                    matchedQuantity = buyQuantity;
                }
            } else {
                if (sellPrice > 0) {
                    midPrice = sellPrice;
                    matchedQuantity = sellQuantity;
                } else {
                    // no matching price found.
                    return;
                }
            }
            buyOrderBook.matched(reportListener, midPrice, matchedQuantity);
            sellOrderBook.matched(reportListener, midPrice, matchedQuantity);

        } finally {
            buyOrderBook.cancelAllRestlessOrders(reportListener);
            sellOrderBook.cancelAllRestlessOrders(reportListener);
        }
    }

    private void cancelAllRestlessOrders() {

    }

    @Override
    public void cancelOrderCommand(CancelOrderCommand cancelOrderCommand) {
        super.cancelOrderCommand(cancelOrderCommand);
    }
}
