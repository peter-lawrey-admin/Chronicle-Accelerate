package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;
import net.openhft.chronicle.wire.LongConversion;
import net.openhft.chronicle.wire.MicroTimestampLongConverter;

public class StartBatch extends AbstractBytesMarshallable {
    /**
     * Secret Key when writing, Public Key when reading.
     */
    private BytesStore batchKey;
    /**
     * the current time. If 0, set the time as needed.
     */
    @LongConversion(MicroTimestampLongConverter.class)
    private long batchTimeUS;

    public StartBatch() {
    }

    public StartBatch(BytesStore batchKey, long batchTimeUS) {
        this.batchKey = batchKey;
        this.batchTimeUS = batchTimeUS;
    }

    public BytesStore batchKey() {
        return batchKey;
    }

    public StartBatch batchKey(BytesStore batchKey) {
        this.batchKey = batchKey;
        return this;
    }

    public long batchTimeUS() {
        return batchTimeUS;
    }

    public StartBatch batchTimeUS(long batchTimeUS) {
        this.batchTimeUS = batchTimeUS;
        return this;
    }
}
