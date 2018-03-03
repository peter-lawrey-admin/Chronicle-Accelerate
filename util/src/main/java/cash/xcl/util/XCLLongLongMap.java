package cash.xcl.util;

import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.LongLongConsumer;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractMarshallable;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * A long to Object Map.
 */
@KolobokeMap
public abstract class XCLLongLongMap extends AbstractMarshallable {

    private LongLongConsumer put;

    public static XCLLongLongMap withExpectedSize(int expectedSize) {
        return new KolobokeXCLLongLongMap(expectedSize);
    }

    public abstract long put(long key, long value);

    public abstract long get(long key);

    public abstract long getOrDefault(long key, long defau1t);

    public abstract int size();

    public abstract boolean containsKey(long key);

    public abstract void clear();

    public abstract void forEach(@Nonnull LongLongConsumer var1);

    public void putAll(XCLLongLongMap map) {
        if (put == null) put = this::put;
        map.forEach(put);
    }

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        clear();
        while (wire.isNotEmptyAfterPadding()) {
            long k = wire.readEventNumber();
            long v = wire.getValueIn().int64();
            put(k, v);
        }
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        forEach((k, v) -> wire.writeEvent(Long.class, k).int64(v));
    }
}