package cash.xcl.api.exch.fix;


/**
 * Generated at software.chronicle.fix.codegen.MessageGenerator.generateHeaderTrailer(MessageGenerator.java:101)
 */
public interface HeaderTrailer extends StandardHeaderTrailer, SenderSubID, TargetSubID {
    default void reset() {
        StandardHeaderTrailer.super.reset();
        senderSubID(null);
        targetSubID(null);
    }

    default void copyTo(StandardHeaderTrailer msg) {
        copyTo((HeaderTrailer) msg);
    }

    default void copyTo(HeaderTrailer msg) {
        StandardHeaderTrailer.super.copyTo(msg);
        if (senderSubID() != null) msg.senderSubID(senderSubID());
        if (targetSubID() != null) msg.targetSubID(targetSubID());
    }
}
