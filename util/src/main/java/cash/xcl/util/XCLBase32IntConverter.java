package cash.xcl.util;

import net.openhft.chronicle.wire.IntConverter;

public class XCLBase32IntConverter implements IntConverter {
    public static final XCLBase32IntConverter INSTANCE = new XCLBase32IntConverter();
    @Override
    public int parse(CharSequence text) {
        return XCLBase32.decodeInt(text);
    }

    public static String asString(int value) {
        return XCLBase32.encodeInt(value);
    }

    @Override
    public void append(StringBuilder text, int value) {
        text.append(asString(value));
    }
}
