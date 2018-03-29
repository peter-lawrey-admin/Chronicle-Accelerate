package cash.xcl.api.util;

import net.openhft.chronicle.wire.IntConverter;

public class XCLBase32UpperIntConverter implements IntConverter {
    @Override
    public int parse(CharSequence text) {
        return XCLBase32.decodeInt(text);
    }

    @Override
    public void append(StringBuilder text, int value) {
        text.append(XCLBase32.encodeIntUpper(value));
    }
}
