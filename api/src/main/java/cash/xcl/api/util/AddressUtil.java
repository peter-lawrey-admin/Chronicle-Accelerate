package cash.xcl.api.util;

import net.openhft.chronicle.bytes.Bytes;

public enum AddressUtil {
    ;
    public static final int CHECK_NUMBER = 37;
    private static final ThreadLocal<Bytes<?>> bytesCache = ThreadLocal.withInitial(() -> Bytes.elasticByteBuffer(14));
    public static final long INVALID_ADDRESS = decode("23456"); // no country will ever have a 2 char code 23 and 456 region

    public static String encode(long address) {
        Bytes<?> addressAsBytes = bytesCache.get().clear();
        XCLBase32.encode(addressAsBytes, address);
        return addressAsBytes.toString();
    }

    public static long decode(String address) {
        return XCLBase32.decode(bytesCache.get().clear().append(address));
    }

    public static boolean isValid(long address) {
        return address % CHECK_NUMBER == 0;
    }

    public static boolean isReserved(long address) {
        return (address & 15) == 0;
    }
}
