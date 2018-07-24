package cash.xcl.util;

import com.koloboke.collect.IntCursor;
import com.koloboke.compile.KolobokeSet;
import net.openhft.chronicle.wire.AbstractMarshallable;

import javax.annotation.Nonnull;

@KolobokeSet
public abstract class XCLIntSet extends AbstractMarshallable {
    public abstract boolean add(int n);

    public abstract boolean remove(int n);

    @Nonnull
    public abstract IntCursor cursor();

}
