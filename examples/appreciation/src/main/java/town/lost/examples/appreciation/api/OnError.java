package town.lost.examples.appreciation.api;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class OnError extends AbstractBytesMarshallable {
    private BytesStore batchKey;
    private long batchTimeUS;
    private String message;

    public void init(BytesStore batchKey, long batchTimeUS, String message) {
        this.batchKey = batchKey;
        this.batchTimeUS = batchTimeUS;
        this.message = message;
    }

    public BytesStore batchKey() {
        return batchKey;
    }

    public OnError batchKey(BytesStore batchKey) {
        this.batchKey = batchKey;
        return this;
    }

    public long batchTimeUS() {
        return batchTimeUS;
    }

    public OnError batchTimeUS(long batchTimeUS) {
        this.batchTimeUS = batchTimeUS;
        return this;
    }

    public String message() {
        return message;
    }

    public OnError message(String message) {
        this.message = message;
        return this;
    }
}
