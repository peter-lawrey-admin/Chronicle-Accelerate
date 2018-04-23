package cash.xcl.util;

// selected sections copied from https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/UnsignedLongs.java
public enum UnsignedLong {
    ;

    private static int[] DIVISOR_MOD_CACHE = new int[101];

    static {
        for (int i = 2; i < DIVISOR_MOD_CACHE.length; i++)
            DIVISOR_MOD_CACHE[i] = (int) (Long.MIN_VALUE % i);
    }

    /**
     * A (self-inverse) bijection which converts the ordering on unsigned longs to the ordering on
     * longs, that is, {@code a <= b} as unsigned longs if and only if {@code flip(a) <= flip(b)} as
     * signed longs.
     */
    private static long flip(long a) {
        return a ^ Long.MIN_VALUE;
    }

    /**
     * Compares the two specified {@code long} values, treating them as unsigned values between {@code
     * 0} and {@code 2^64 - 1} inclusive.
     * <p>
     * <p><b>Java 8 users:</b> use {@link Long#compareUnsigned(long, long)} instead.
     *
     * @param a the first unsigned {@code long} to compare
     * @param b the second unsigned {@code long} to compare
     * @return a negative value if {@code a} is less than {@code b}; a positive value if {@code a} is
     * greater than {@code b}; or zero if they are equal
     */
    public static int compare(long a, long b) {
        return Long.compare(flip(a), flip(b));
    }

    public static long mod(long dividend, long divisor) {
        if (divisor < 0) { // i.e., divisor >= 2^63:
            if (compare(dividend, divisor) < 0) {
                return dividend; // dividend < divisor
            } else {
                return dividend - divisor; // dividend >= divisor
            }
        }

        // Optimization - use signed modulus if dividend < 2^63
        if (dividend >= 0) {
            return dividend % divisor;
        }

        long adjust = divisor < DIVISOR_MOD_CACHE.length
                ? DIVISOR_MOD_CACHE[(int) divisor]
                : Long.MIN_VALUE % divisor;
        long l = (dividend - Long.MIN_VALUE) % divisor - adjust;
        return l >= divisor ? l - divisor : l;
    }
}
