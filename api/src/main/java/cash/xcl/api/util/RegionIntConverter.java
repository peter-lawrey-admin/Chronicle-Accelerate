package cash.xcl.api.util;

import net.openhft.chronicle.core.util.StringUtils;
import net.openhft.chronicle.wire.IntConverter;

public class RegionIntConverter implements IntConverter {
    public static RegionIntConverter INSTANCE = new RegionIntConverter();

    @Override
    public int parse(CharSequence text) {
        return StringUtils.isEqual(text, CountryRegion.MAIN_CODE)
                ? 0
                : XCLBase32.decodeInt(text + "Z");
    }

    @Override
    public void append(StringBuilder text, int value) {
        if (value == 0) {
            text.append("0000");
        }
        String str = XCLBase32.encodeInt2(value).toUpperCase();
        text.append(str, 0, str.length() - 1);
    }

    public String asString(int region) {
        if (region == 0) {
            return "0000";
        }
        String s = XCLBase32.encodeInt2(region).toUpperCase();
        return s.substring(0, s.length() - 1);
    }
}
