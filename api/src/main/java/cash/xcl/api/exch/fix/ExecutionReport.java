package cash.xcl.api.exch.fix;

import net.openhft.chronicle.bytes.Bytes;

/**
 * Generated at software.chronicle.fix.codegen.MessageGenerator.generateMessage(MessageGenerator.java:197)
 */
public interface ExecutionReport extends HeaderTrailer, OrderID, ClOrdID, ExecID, ExecTransType, ExecType, OrdStatus, Account, SettlmntTyp, SecurityID, IDSource, Side, OrderQty, OrdType, Price, Currency, TimeInForce, LastShares, LastPx, LastMkt, LeavesQty, CumQty, AvgPx, TradeDate, TransactTime, SettlCurrency, HandlInst, CreatedNS {
//    static ExecutionReport newExecutionReport(Bytes bytes) {
//        MessageGenerator mg = new MessageGenerator(MessageManifest.ExecutionReport);
//        mg.bytes(bytes);
//        return mg;
//    }

    default void reset() {
        HeaderTrailer.super.reset();
        orderID(null);
        clOrdID(null);
        execID(null);
        execTransType(FixMessage.UNSET_CHAR);
        execType(FixMessage.UNSET_CHAR);
        ordStatus(FixMessage.UNSET_CHAR);
        account(null);
        settlmntTyp(FixMessage.UNSET_CHAR);
        securityID(null);
        idSource(null);
        side(FixMessage.UNSET_CHAR);
        orderQty(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        ordType(FixMessage.UNSET_CHAR);
        price(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        currency(null);
        timeInForce(FixMessage.UNSET_CHAR);
        lastShares(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        lastPx(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        lastMkt(null);
        leavesQty(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        cumQty(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        avgPx(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        tradeDate(null);
        transactTime(FixMessage.UNSET_LONG);
        settlCurrency(null);
        handlInst(FixMessage.UNSET_CHAR);
        createdNS(FixMessage.UNSET_LONG);
    }

    default void copyTo(StandardHeaderTrailer msg) {
        copyTo((ExecutionReport) msg);
    }

    default void copyTo(ExecutionReport msg) {
        HeaderTrailer.super.copyTo(msg);
        msg.orderID(orderID());
        msg.clOrdID(clOrdID());
        msg.execID(execID());
        msg.execTransType(execTransType());
        msg.execType(execType());
        msg.ordStatus(ordStatus());
        msg.account(account());
        msg.settlmntTyp(settlmntTyp());
        msg.securityID(securityID());
        msg.idSource(idSource());
        msg.side(side());
        msg.orderQty(orderQty(), orderQty_dp());
        msg.ordType(ordType());
        msg.price(price(), price_dp());
        if (currency() != null) msg.currency(currency());
        if (timeInForce() != FixMessage.UNSET_CHAR) msg.timeInForce(timeInForce());
        if (!Double.isNaN(lastShares())) msg.lastShares(lastShares(), lastShares_dp());
        if (!Double.isNaN(lastPx())) msg.lastPx(lastPx(), lastPx_dp());
        if (lastMkt() != null) msg.lastMkt(lastMkt());
        msg.leavesQty(leavesQty(), leavesQty_dp());
        msg.cumQty(cumQty(), cumQty_dp());
        msg.avgPx(avgPx(), avgPx_dp());
        msg.tradeDate(tradeDate());
        msg.transactTime(transactTime());
        if (settlCurrency() != null) msg.settlCurrency(settlCurrency());
        if (handlInst() != FixMessage.UNSET_CHAR) msg.handlInst(handlInst());
        if (createdNS() != FixMessage.UNSET_LONG) msg.createdNS(createdNS());
    }
}
