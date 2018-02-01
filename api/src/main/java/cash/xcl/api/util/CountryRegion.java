package cash.xcl.api.util;

import net.openhft.chronicle.wire.AbstractMarshallable;

public class CountryRegion extends AbstractMarshallable {

    private final String countryName;
    private final String regionCode;
    private final String regionName;
    private final String countryCode3;
    private final String countryCode2;

    private CountryRegion(String countryName, String countryCode2, String countryCode3, String regionCode, String regionName) {
        this.countryName = countryName;
        this.countryCode2 = countryCode2;
        this.countryCode3 = countryCode3;
        this.regionCode = regionCode;
        this.regionName = regionName;
    }

    public String countryName() {
        return countryName;
    }

    public String countryCode2() {
        return countryCode2;
    }

    public String countryCode3() {
        return countryCode3;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String regionCode() {
        return regionCode;
    }

    public String regionName() {
        return regionName;
    }
}
