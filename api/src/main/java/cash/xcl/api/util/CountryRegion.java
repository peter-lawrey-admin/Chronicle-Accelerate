package cash.xcl.api.util;

import cash.xcl.util.RegionIntConverter;
import cash.xcl.util.Validators;
import cash.xcl.util.XCLBase32;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractMarshallable;
import net.openhft.chronicle.wire.WireIn;

import java.util.Arrays;

import static cash.xcl.util.Validators.notNullOrEmpty;

public class CountryRegion extends AbstractMarshallable {

    public static final int MAIN_CHAIN = RegionIntConverter.MAIN_REGINT;
    static final String[] NO_STRINGS = new String[0];
    private final String countryName;
    private final String regionCode;
    private final String regionName;
    private final String countryCode3;
    private final String countryCode2;
    private transient String[] overlappedSuffixes;
    private transient String regionCodeBase32;
    private transient long regionCodeAddress;

    public CountryRegion(String countryName, String countryCode2, String countryCode3, String regionCode, String regionName) {
        this.countryName = notNullOrEmpty(countryName).trim();
        this.countryCode2 = countryCode2.trim();
        if (this.countryCode2.length() != 2) {
            throw new IllegalArgumentException(countryCode2);
        }
        this.countryCode3 = countryCode3.trim();
        if (this.countryCode3.length() != 3) {
            throw new IllegalArgumentException(countryCode3);
        }
        this.regionCode = regionCode.trim();
        if ((4 > this.regionCode.length()) || (this.regionCode.length() > 6)) {
            throw new IllegalArgumentException(regionCode);
        }
        this.regionName = notNullOrEmpty(regionName.trim());
        this.regionCodeBase32 = XCLBase32.normalize(this.regionCode);
        this.regionCodeAddress = XCLBase32.decode(regionCodeBase32);
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

    public int regionCodeLength() {
        return regionCode.length() - 3;
    }

    public String[] overlappedSuffixes() {
        return overlappedSuffixes;
    }

    public void overlappedSuffixes(String[] overlappedSufixes) {
        this.overlappedSuffixes = Validators.notNull(overlappedSufixes);
        Arrays.sort(overlappedSufixes);
    }

    public String regionCodeBase32() {
        return regionCodeBase32;
    }

    @Override
    public String toString() {
        return regionCode;
    }

    public String[] getOverlappedSuffixes() {
        return overlappedSuffixes();
    }

    public String getCountryName() {
        return countryName();
    }

    public String getCountryCode2() {
        return countryCode2();
    }

    public String getCountryCode3() {
        return countryCode3();
    }

    public String getRegionName() {
        return regionName();
    }

    public String getRegionCodeBase32() {
        return regionCodeBase32();
    }

    public void addOverlappingSuffix(String suffix) {
        if (overlappedSuffixes == null)
            overlappedSuffixes = NO_STRINGS;
        int length = overlappedSuffixes.length;
        overlappedSuffixes = Arrays.copyOf(overlappedSuffixes, length + 1);
        overlappedSuffixes[length] = suffix;
    }

    @Override
    public void readMarshallable(WireIn wireIn) throws IORuntimeException {
        super.readMarshallable(wireIn);
        overlappedSuffixes = NO_STRINGS;
        regionCodeBase32 = XCLBase32.normalize(regionCode);
    }

    public long regionCodeAddress() {
        return regionCodeAddress;
    }
}
