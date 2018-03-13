package cash.xcl.api.exch.fix;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created by Peter Lawrey on 19/12/16.
 */
public interface FixConstants {
    double UNSET_DOUBLE = Double.NaN;
    Double _UNSET_DOUBLE = UNSET_DOUBLE;
    long UNSET_LONG = Long.MIN_VALUE;
    Long _UNSET_LONG = UNSET_LONG;
    char UNSET_CHAR = '\uFFFF'; // not a character https://en.wikipedia.org/wiki/Specials_(Unicode_block)
    Character _UNSET_CHAR = UNSET_CHAR;
    int UNSET_DP = Integer.MAX_VALUE; // not a character https://en.wikipedia.org/wiki/Specials_(Unicode_block)
    Integer _UNSET_DP = UNSET_DP; // not a character https://en.wikipedia.org/wiki/Specials_(Unicode_block)
    TimeUnit UNSET_TSR = null;

    /**
     * Check whether a value represents an UNSET value.
     *
     * @param o to check
     * @return true if UNSET.
     */
    static boolean isUnset(@Nullable Object o) {
        return o == null ||
                _UNSET_DOUBLE.equals(o) ||
                _UNSET_LONG.equals(o) ||
                _UNSET_CHAR.equals(o);
    }
}
