package cash.xcl.util;

import net.openhft.chronicle.wire.IntConverter;

public class RegionIntConverter implements IntConverter {
    public static final String MAIN_CODE = "4main";
    public static final int MAIN_REGINT = XCLBase32.decodeInt(MAIN_CODE);
    public static final RegionIntConverter INSTANCE = new RegionIntConverter();

    @Override
    public int parse(CharSequence text) {
        return XCLBase32.decodeInt(text);
    }

    @Override
    public void append(StringBuilder text, int value) {
        String str = XCLBase32.encodeIntUpper(value).toUpperCase();
        text.append(str);
    }

    public String asString(int region) {
        return XCLBase32.encodeIntUpper(region);
    }
}
