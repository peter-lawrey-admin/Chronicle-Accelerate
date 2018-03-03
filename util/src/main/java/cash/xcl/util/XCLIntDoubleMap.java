package cash.xcl.util;

import com.koloboke.compile.KolobokeMap;
import com.koloboke.function.IntDoubleConsumer;
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
public abstract class XCLIntDoubleMap extends AbstractMarshallable {

    public static XCLIntDoubleMap withExpectedSize(int expectedSize) {
        return new KolobokeXCLIntDoubleMap(expectedSize);
    }

    public abstract double put(int key, double value);

    public abstract double get(int key);

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract boolean containsKey(int key);

    public abstract void clear();

    public abstract void forEach(@Nonnull IntDoubleConsumer var1);

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        clear();
        while (wire.isNotEmptyAfterPadding()) {
            int k = (int) wire.readEventNumber();
            double v = wire.getValueIn().float64();
            put(k, v);
        }
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        forEach((k, v) -> wire.writeEventId(k).float64(v));
    }

    public void putAll(XCLIntDoubleMap newBalances) {
        newBalances.forEach(this::put);
    }
}