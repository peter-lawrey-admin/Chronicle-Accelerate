package cash.xcl.api.util;

import net.openhft.chronicle.wire.LongConverter;

public class XCLBase32LongConverter implements LongConverter {
    @Override
    public long parse(CharSequence text) {
        return XCLBase32.decode(text);
    }

    @Override
    public void append(StringBuilder text, long value) {
        text.append(XCLBase32.encode(value));
    }
}
