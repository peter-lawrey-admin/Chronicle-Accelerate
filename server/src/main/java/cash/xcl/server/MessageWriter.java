package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import net.openhft.chronicle.core.io.Closeable;

public interface MessageWriter extends AllMessagesLookup, Closeable {
    Runnable[] runnables();
}
