package cash.xcl.util;

import com.koloboke.compile.KolobokeMap;
import net.openhft.chronicle.wire.AbstractMarshallable;

@KolobokeMap
public abstract class XCLShortObjMap<V> extends AbstractMarshallable {
    public abstract V get(short key);
}
