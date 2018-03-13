package cash.xcl.api.exch.fix;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.Wires;
import org.jetbrains.annotations.NotNull;


/**
 * Created by Peter Lawrey on 18/12/16.
 */
public interface FixMessage extends FixConstants {
    @NotNull
    static <T> T deepCopy(T t) {
        //noinspection unchecked
        return (T) Wires.deepCopy((Marshallable) t);
    }

    void reset();

    default char msgType() {
        throw new UnsupportedOperationException();
    }

    default void msgType(char msgType) {
        throw new UnsupportedOperationException();
    }

    Bytes bytes();
}
