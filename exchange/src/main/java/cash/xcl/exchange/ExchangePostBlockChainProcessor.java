package cash.xcl.exchange;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CancelOrderCommand;
import cash.xcl.api.dto.ExecutionReportEvent;
import cash.xcl.api.dto.NewOrderCommand;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.exchange.model.Order;
import cash.xcl.exchange.model.OrderBook;
import cash.xcl.server.LocalPostBlockChainProcessor;

public class ExchangePostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final AllMessages reportListener;
    private OrderBook buyOrderBook;
    private OrderBook sellOrderBook;
    private long orderId = 0;

    public ExchangePostBlockChainProcessor(long address, String symbol1symbol2) {
        super(address);
        reportListener = new EPBCPResponseMessages(address);
        buyOrderBook = new OrderBook(true, symbol1symbol2);
        sellOrderBook = new OrderBook(false, symbol1symbol2);
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
        long orderId = ++this.orderId; // uniqueness by order added.
        (newOrderCommand.isBuy() ? buyOrderBook : sellOrderBook)
                .addOrder(orderId, newOrderCommand);
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
                if (buy.price() < sell.price()) {
                    int cmp = compare(buyQuantity, sellQuantity, 1e-6);
                    if (cmp > 0) {
                        sellQuantity += Math.min(buyQuantity - sellQuantity, sell.quantity());
                        sellPrice = sell.price();
                        continue;
                    }
                    if (cmp < 0) {
                        buyQuantity += Math.min(sellQuantity - buyQuantity, buy.quantity());
                        buyPrice = buy.price();
                        continue;
                    }

                    // false is either is NaN
                    if (Double.isNaN(buyPrice))
                        buyPrice = buy.price();
                    if (Double.isNaN(sellPrice))
                        sellPrice = sell.price();
                    break;
                }
                int cmp = compare(buyQuantity, sellQuantity, 1e-6);
                if (cmp >= 0) {
                    sellQuantity += sell.quantity();
                    sellPrice = sell.price();
                    sellOrderBook.advanceNext();
                }
                if (cmp <= 0) {
                    buyQuantity += buy.quantity();
                    buyPrice = buy.price();
                    buyOrderBook.advanceNext();
                }
            }
            double midPrice, matchedQuantity;
            if (buyQuantity > 0) {
                if (sellQuantity > 0) {
                    midPrice = (buyPrice + sellPrice) / 2;
                    matchedQuantity = Math.min(buyQuantity, sellQuantity);
                } else {
                    midPrice = buyPrice;
                    matchedQuantity = buyQuantity;
                }
            } else {
                if (sellQuantity > 0) {
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
        buyOrderBook.cancelOrder(reportListener, cancelOrderCommand);
        sellOrderBook.cancelOrder(reportListener, cancelOrderCommand);
    }

    private class EPBCPResponseMessages extends AbstractAllMessages {
        EPBCPResponseMessages(long address) {
            super(address);
        }

        @Override
        public void executionReportEvent(ExecutionReportEvent executionReportEvent) {
            executionReportEvent.sourceAddress(address);
            executionReportEvent.eventTime(timeProvider.currentTimeMicros());
            ExchangePostBlockChainProcessor.this.to(executionReportEvent.clientAddress())
                    .executionReportEvent(executionReportEvent);
        }
    }
}
