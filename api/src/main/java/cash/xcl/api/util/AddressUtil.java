package cash.xcl.api.util;

import cash.xcl.util.XCLBase32;
import net.openhft.chronicle.bytes.Bytes;

public enum AddressUtil {
    ;
    public static final int CHECK_NUMBER = 37;
    private static final ThreadLocal<Bytes<?>> bytesCache = ThreadLocal.withInitial(() -> Bytes.elasticByteBuffer(14));
    public static final long INVALID_ADDRESS = decode("."); // multiples of 16 are reserved

    public static String encode(long address) {
        Bytes<?> addressAsBytes = bytesCache.get().clear();
        XCLBase32.encode(addressAsBytes, address);
        return addressAsBytes.toString();
    }

    public static long decode(String address) {
        return XCLBase32.decode(address);
    }

    public static boolean isValid(long address) {
        return address >= 0
                ? address % CHECK_NUMBER == 0
                : address % CHECK_NUMBER == -12; // number has overflown so need to check as if unsigned.
    }

    public static boolean isReserved(long address) {
        return (address & 15) == 0;
    }
}
