package cash.xcl.api.exch.fix;



import java.util.concurrent.TimeUnit;

/**
 * Generated at software.chronicle.fix.codegen.MessageGenerator.generateHeaderTrailer(MessageGenerator.java:103)
 */
public interface StandardHeaderTrailer extends FixMessage, SenderCompID, TargetCompID, MsgSeqNum, PossDupFlag, PossResend, SendingTime {
    default void reset() {
        senderCompID(null);
        targetCompID(null);
        msgSeqNum(FixMessage.UNSET_LONG);
        possDupFlag(FixMessage.UNSET_CHAR);
        possResend(FixMessage.UNSET_CHAR);
        sendingTime(FixMessage.UNSET_LONG, FixMessage.UNSET_TSR);
    }

    default void copyTo(StandardHeaderTrailer msg) {
        if (senderCompID() != null) msg.senderCompID(senderCompID());
        if (targetCompID() != null) msg.targetCompID(targetCompID());
        if (msgSeqNum() != FixMessage.UNSET_LONG) msg.msgSeqNum(msgSeqNum());
        if (possDupFlag() != FixMessage.UNSET_CHAR) msg.possDupFlag(possDupFlag());
        if (possResend() != FixMessage.UNSET_CHAR) msg.possResend(possResend());
        msg.sendingTime(sendingTime(), sendingTime_tsr());
    }

    default TimeUnit internalTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    default void validate() {}
}
