package cash.xcl.api.dto;

import com.koloboke.function.LongLongConsumer;
import net.openhft.chronicle.bytes.BytesOut;

class LongU32Writer implements LongLongConsumer {
    BytesOut<?> bytes;

    @Override
    public void accept(long k, long v) {
        bytes.writeLong(k).writeUnsignedInt(v);
    }
}
