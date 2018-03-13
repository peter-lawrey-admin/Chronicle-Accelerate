package cash.xcl.api.exch.fix;

import net.openhft.chronicle.bytes.Bytes;

/**
 * Generated at software.chronicle.fix.codegen.MessageGenerator.generateMessage(MessageGenerator.java:197)
 */
public interface NewOrderSingle extends HeaderTrailer, ClOrdID, Side, OrdType, Symbol, HandlInst, TransactTime, Account, OrderQty, Price, TimeInForce, MaturityMonthYear, SecurityType, IDSource, SecurityID, CreatedNS {


//    static NewOrderSingle newNewOrderSingle(Bytes bytes) {
//        MessageGenerator mg = new MessageGenerator(MessageManifest.NewOrderSingle);
//        mg.bytes(bytes);
//        return mg;
//    }

    default void reset() {
        HeaderTrailer.super.reset();
        clOrdID(null);
        side(FixMessage.UNSET_CHAR);
        ordType(FixMessage.UNSET_CHAR);
        symbol(null);
        handlInst(FixMessage.UNSET_CHAR);
        transactTime(FixMessage.UNSET_LONG);
        account(null);
        orderQty(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        price(FixMessage.UNSET_DOUBLE, FixMessage.UNSET_DP);
        timeInForce(FixMessage.UNSET_CHAR);
        maturityMonthYear(null);
        securityType(null);
        idSource(null);
        securityID(null);
        createdNS(FixMessage.UNSET_LONG);
    }

    default void copyTo(StandardHeaderTrailer msg) {
        copyTo((NewOrderSingle) msg);
    }

    default void copyTo(NewOrderSingle msg) {
        HeaderTrailer.super.copyTo(msg);
        msg.clOrdID(clOrdID());
        msg.side(side());
        msg.ordType(ordType());
        msg.symbol(symbol());
        msg.handlInst(handlInst());
        msg.transactTime(transactTime());
        if (account() != null) msg.account(account());
        if (!Double.isNaN(orderQty())) msg.orderQty(orderQty(), orderQty_dp());
        if (!Double.isNaN(price())) msg.price(price(), price_dp());
        if (timeInForce() != FixMessage.UNSET_CHAR) msg.timeInForce(timeInForce());
        if (maturityMonthYear() != null) msg.maturityMonthYear(maturityMonthYear());
        if (securityType() != null) msg.securityType(securityType());
        if (idSource() != null) msg.idSource(idSource());
        if (securityID() != null) msg.securityID(securityID());
        if (createdNS() != FixMessage.UNSET_LONG) msg.createdNS(createdNS());
    }
}
