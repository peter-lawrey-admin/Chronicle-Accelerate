package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;
import net.openhft.chronicle.wire.LongConversion;
import net.openhft.chronicle.wire.MicroTimestampLongConverter;

public class StartBlock extends AbstractBytesMarshallable {
    /**
     * Secret Key when writing, Public Key when reading.
     */
    private BytesStore blockKey;
    /**
     * the current time. If 0, set the time as needed.
     */
    @LongConversion(MicroTimestampLongConverter.class)
    private long blockTimeUS;

    public StartBlock() {
    }

    public StartBlock(BytesStore blockKey, long blockTimeUS) {
        init(blockKey, blockTimeUS);
    }

    public void init(BytesStore blockKey, long blockTimeUS) {
        this.blockKey = blockKey;
        this.blockTimeUS = blockTimeUS;
    }

    public BytesStore blockKey() {
        return blockKey;
    }

    public StartBlock blockKey(BytesStore blockKey) {
        this.blockKey = blockKey;
        return this;
    }

    public long blockTimeUS() {
        return blockTimeUS;
    }

    public StartBlock blockTimeUS(long blockTimeUS) {
        this.blockTimeUS = blockTimeUS;
        return this;
    }
}
