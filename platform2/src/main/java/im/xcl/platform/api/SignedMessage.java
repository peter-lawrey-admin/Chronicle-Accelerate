package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;

public interface SignedMessage {
    /**
     * Once a message is signed it can't be modified, only read.
     * <p>
     * It cannot be sent until it is signed.
     *
     * @return true if signed, false if not signed
     */
    boolean signed();

    /**
     * Unique address for the sender. This should be the last 8 bytes of the public key.
     *
     * @return a unique id for this server.
     */
    long address();

    /**
     * A microsecond precision, unique timestamp
     * <p>
     * NOTE: If the writing system doesn't have a micro-second accurate clock,
     * the most accurate clock should be used and increment the timestamp in the cause of any collision
     *
     * @return a unique microsecond timestamp for an address. Must be monotonically increasing.
     */
    long timestampUS();

    /**
     * @return the public key if it is embedded in the message, otherwise it will need to be implied from the address.
     */
    BytesStore publicKey();

    /**
     * This will signed the message as well as set the public key (if any) and address
     *
     * @param secretKey to signed this message with.
     */
    void sign(BytesStore secretKey);
}
