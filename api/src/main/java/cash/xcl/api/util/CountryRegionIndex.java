package cash.xcl.api.util;

import cash.xcl.util.XCLBase32;
import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.wire.CSVWire;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class CountryRegionIndex {
    private final Map<String, CountryRegion> indexByBase32 = new LinkedHashMap<>(8192);
    private final Collection<CountryRegion> countryRegions = Collections.unmodifiableCollection(indexByBase32.values());

    public CountryRegionIndex() {
        this("cash/xcl/api/util/country_state_codes.csv",
                "cash/xcl/api/util/excluded.yaml");
    }

    public CountryRegionIndex(String resourceName) {
        this(resourceName, null);
    }

    public CountryRegionIndex(String resourceName, String excludedName) {
        try {
            Set<String> excluded = excludedName == null
                    ? Collections.emptySet()
                    : new HashSet<>(Arrays.asList(Marshallable.fromFile(String[].class, excludedName)));
            CSVWire wire = new CSVWire(BytesUtil.readFile(resourceName));
            while (true) {
                wire.consumeWhiteSpace();
                if (wire.isEmpty())
                    break;

                CountryRegion cr = ObjectUtils.newInstance(CountryRegion.class);
                cr.readMarshallable(wire);
                if (excluded.contains(cr.getRegionCode()))
                    continue;
//                System.out.println(cr);
                String key = cr.regionCodeBase32().toUpperCase();
                if (indexByBase32.containsKey(key))
                    Jvm.warn().on(getClass(), "duplicate key: " + key + " for " + indexByBase32.get(key) + " and " + cr);
                indexByBase32.put(key, cr);
            }
            for (String codes : indexByBase32.keySet()) {
                String search = codes;
                while (search.length() > 3) {
                    search = search.substring(0, search.length() - 1);
                    CountryRegion cr2 = indexByBase32.get(search);
                    if (cr2 != null) {
//                        System.out.println(cr2);
                        cr2.addOverlappingSuffix(codes.substring(search.length()));
                    }
                }
            }
            // sort the results
            for (CountryRegion cr : indexByBase32.values()) {
                Arrays.sort(cr.overlappedSuffixes());
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public boolean isValidRegion(String regionCode) {
        return regionCode != null && indexByBase32.containsKey(normalize(regionCode));
    }

    @NotNull
    private String normalize(String regionCode) {
        return XCLBase32.normalize(regionCode).toUpperCase();
    }

    public CountryRegion getRegion(String regionCode) {
        return indexByBase32.get(normalize(regionCode));
    }

    public CountryRegion getFromBase32(String regionCodeBase32NoDash) {
        return indexByBase32.get(normalize(regionCodeBase32NoDash));
    }

    public CountryRegion matchCountryRegion(String encodedAddress) {
        encodedAddress = normalize(encodedAddress);
        String possibleCode = encodedAddress.substring(0, Math.min(6, encodedAddress.length()));
        while (possibleCode.length() > 2) {
            CountryRegion countryRegion = indexByBase32.get(possibleCode);
            if (countryRegion != null) {
                return countryRegion;
            } else {
                possibleCode = possibleCode.substring(0, possibleCode.length() - 1);
            }
        }
        return null;
    }

    public Collection<CountryRegion> countryRegions() {
        return countryRegions;
    }
}
