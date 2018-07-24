package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.salt.Ed25519;

import java.util.ArrayList;
import java.util.List;

public class VanillaSignedMessage {
    public static final int LENGTH = 0;
    public static final int MAGIC = LENGTH + Integer.BYTES;
    public static final int SIGNATURE = MAGIC + Integer.BYTES;
    public static final int PUBLIC_KEY = SIGNATURE + Ed25519.SIGNATURE_LENGTH;
    public static final int MICRO_TS = PUBLIC_KEY + Ed25519.PUBLIC_KEY_LENGTH;
    public static final int MESSAGE_START = MICRO_TS + Long.BYTES;
    public static final int TRANSACTION_BLOCK = 0x01010101;

    private Bytes sigAndMsg;
    private SignedMessageFormat format;
    private List<Instruction> instructions = new ArrayList<>();
    private InstructionCache instructionCache;
}
