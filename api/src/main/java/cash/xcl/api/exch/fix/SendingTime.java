package cash.xcl.api.exch.fix;

import java.util.concurrent.TimeUnit;

/**
 * Can no longer be generated since it needs to handle both presence and absence of timestamp resolution field.
 */
public interface SendingTime {
    /**
     * Tag number for this field
     */
    int FIELD = 52;

    /**
     * Setter for clients whose code was generated with 'includeTimestampResolution' option.
     * NOTE - at least one of these setters should have been overridden by generated code.
     * @param sendingTime &gt; FIX TAG 52 interpreted as time in units of internalTimeUnit() since the epoch.
     * @param sendingTime_tsr Timestamp resolution.

     */
    default void sendingTime(long sendingTime, TimeUnit sendingTime_tsr) {
        sendingTime(sendingTime);
    }

    /**
     * Setter for clients whose code was generated before or without 'includeTimestampResolution' option.
     * NOTE - at least one of these setters should have been overridden by generated code.
     * @param sendingTime &gt; FIX TAG 52
     */
    default void sendingTime(long sendingTime) {
        sendingTime(sendingTime, sendingTime_tsr());
    }

    default long sendingTime() {
        throw new UnsupportedOperationException();
    }

    TimeUnit sendingTime_tsr();

}
