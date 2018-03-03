package cash.xcl.util;

import com.koloboke.compile.KolobokeMap;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractMarshallable;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.ObjDoubleConsumer;

/**
 * A long to Object Map.
 */
@KolobokeMap
public abstract class XCLStringDoubleMap extends AbstractMarshallable {


    public static XCLStringDoubleMap withExpectedSize(int expectedSize) {
        return new KolobokeXCLStringDoubleMap(expectedSize);
    }

    public abstract double put(String key, double value);

    public abstract double getDouble(String key);

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract boolean containsKey(String key);

    public abstract void clear();

    public abstract void forEach(@Nonnull ObjDoubleConsumer<? super String> var1);

    @Override
    public void readMarshallable(@NotNull WireIn wire) throws IORuntimeException {
        clear();
        while (wire.isNotEmptyAfterPadding()) {
            String k = wire.readEvent(String.class);
            double v = wire.getValueIn().float64();
            put(k, v);
        }
    }

    @Override
    public void writeMarshallable(@NotNull WireOut wire) {
        forEach((k, v) -> wire.write(k).float64(v));
    }

    public void putAll(XCLStringDoubleMap newBalances) {
        newBalances.forEach(this::put);
    }
}