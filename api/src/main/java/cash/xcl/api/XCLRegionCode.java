package cash.xcl.api;

public enum XCLRegionCode {
    ;

    public static int addressMaskToCode(long address, long mask) {
        int bits = Long.numberOfLeadingZeros(~mask);
        assert bits + Long.numberOfTrailingZeros(mask) == 64;
        assert bits < 27;
        int topBits = (int) ((address & mask) >>> 32);
        return topBits | bits;
    }

    public static long codeToAddress(int code) {
        return (long) code << 32;
    }

    public static long codeToMask(int code) {
        int bits = code & 31;
        return bits == 0 ? 0L : -1L << -bits;
    }
}
