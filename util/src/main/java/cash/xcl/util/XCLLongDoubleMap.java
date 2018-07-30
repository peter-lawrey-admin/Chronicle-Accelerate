package cash.xcl.util;

import com.koloboke.compile.KolobokeMap;
import net.openhft.chronicle.wire.AbstractMarshallable;

@KolobokeMap
public abstract class XCLLongDoubleMap extends AbstractMarshallable {
    public static XCLLongDoubleMap withExpectedSize(int expectedSize) {
        return new KolobokeXCLLongDoubleMap(expectedSize);
    }

    public abstract void justPut(long key, double value);

    public abstract double getOrDefault(long key, double defau1t);

    public abstract String toString();

}
