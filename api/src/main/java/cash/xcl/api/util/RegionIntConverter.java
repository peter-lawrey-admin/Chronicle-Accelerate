package cash.xcl.api.util;

import net.openhft.chronicle.core.util.StringUtils;
import net.openhft.chronicle.wire.IntConverter;

public class RegionIntConverter implements IntConverter {
    public static RegionIntConverter INSTANCE = new RegionIntConverter();

    @Override
    public int parse(CharSequence text) {
        return StringUtils.isEqual(text, CountryRegion.MAIN_CODE)
                ? 0
                : XCLBase32.decodeInt(text);
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
