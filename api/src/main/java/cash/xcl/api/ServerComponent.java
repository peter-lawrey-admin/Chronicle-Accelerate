package cash.xcl.api;

import net.openhft.chronicle.core.io.Closeable;

public interface ServerComponent extends Closeable {
    void allMessagesLookup(AllMessagesLookup lookup);
}
