package im.xcl.platform.api;

import net.openhft.chronicle.wire.HexadecimalLongConverter;
import net.openhft.chronicle.wire.LongConversion;

public interface MessageRouter<T> {
    long DEFAULT_CONNECTION = 0L;

    T to(@LongConversion(HexadecimalLongConverter.class) long address);
}
