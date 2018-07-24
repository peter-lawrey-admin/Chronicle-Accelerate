package im.xcl.platform.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;
import net.openhft.chronicle.wire.LongConversion;
import net.openhft.chronicle.wire.MicroTimestampLongConverter;

public class StartBlock extends AbstractBytesMarshallable {
    /**
     * Secret Key when writing, Public Key when reading.
     */
    BytesStore blockKey;
    /**
     * the current time. If 0, set the time as needed.
     */
    @LongConversion(MicroTimestampLongConverter.class)
    long blockTimeUS;
}
