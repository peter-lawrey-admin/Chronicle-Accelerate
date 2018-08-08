package im.xcl.platform.util;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.salt.Ed25519;

public enum XCLUtil {
    ;

    public static long toAddress(BytesStore publicKey) {
        return publicKey.readLong(Ed25519.PUBLIC_KEY_LENGTH - Long.BYTES);
    }
}
