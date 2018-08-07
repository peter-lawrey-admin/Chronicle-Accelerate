package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;

public interface DtoParser<T> {
    void parseOne(Bytes bytes, T listener);
}
