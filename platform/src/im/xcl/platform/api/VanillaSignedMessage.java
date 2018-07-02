package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.salt.Ed25519;

public class VanillaSignedMessage {
    private static final int LENGTH = 0;
    private static final int MAGIC = LENGTH + Integer.BYTES;
    private static final int SIGNATURE = MAGIC + Integer.BYTES;
    private static final int SOURCE_KEY = SIGNATURE + Ed25519.SIGNATURE_LENGTH;
    private static final int DEST_KEY = SOURCE_KEY + Ed25519.PUBLIC_KEY_LENGTH;
    private static final int MICRO_TS = DEST_KEY + Ed25519.PUBLIC_KEY_LENGTH;
    private static final int MESSAGE_START = MICRO_TS + Long.BYTES;

    private Bytes sigAndMsg;

}
