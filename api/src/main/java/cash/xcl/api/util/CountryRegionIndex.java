package cash.xcl.api.util;

import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.wire.CSVWire;
import net.openhft.chronicle.wire.Marshallable;

import java.io.IOException;
import java.util.*;

public class CountryRegionIndex {
    private final Map<String, CountryRegion> indexByBase32 = new LinkedHashMap<>();
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
            Set<String> excluded;
            if (excludedName == null)
                excluded = Collections.emptySet();
            else
                excluded = new HashSet<>(Arrays.asList(Marshallable.fromFile(String[].class, excludedName)));
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
                String key = cr.regionCodeBase32();
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
        return regionCode != null && indexByBase32.containsKey(XCLBase32.normalize(regionCode));
    }

    public CountryRegion getRegion(String regionCode) {
        return indexByBase32.get(XCLBase32.normalize(regionCode));
    }

    public CountryRegion getFromBase32(String regionCodeBase32NoDash) {
        return indexByBase32.get(regionCodeBase32NoDash);
    }

    public CountryRegion matchCountryRegion(String encodedAddress) {
        String possibleCode = encodedAddress.substring(0, Math.min(5, encodedAddress.length()));
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

/*
    @Override
    public Iterator<CountryRegion> iterator() {
        return Collections.unmodifiableCollection(index.values()).iterator();
    }
*/

    private static class RegionCodesByCountry {
        final Map<String, CountryRegion> regionCodeToCountryRegion = new LinkedHashMap<>();
        final int[] regionCountByCodeSize = {0, 0, 0};

        public RegionCodesByCountry(String countryCode) {
        }

        void addRegion(CountryRegion countryRegion) {
            regionCodeToCountryRegion.put(countryRegion.getRegionCode(), countryRegion);
            regionCountByCodeSize[countryRegion.regionCodeLength() - 1] += 1;
        }

        boolean fixedSizeRegionCodes() {
            assert regionCountByCodeSize[0] >= 0;
            assert regionCountByCodeSize[1] >= 0;
            assert regionCountByCodeSize[2] >= 0;
            if ((regionCountByCodeSize[0] == 0) && (regionCountByCodeSize[1] == 0)) {
                return true;
            }
            if ((regionCountByCodeSize[0] == 0) && (regionCountByCodeSize[2] == 0)) {
                return true;
            }
            if ((regionCountByCodeSize[1] == 0) && (regionCountByCodeSize[2] == 0)) {
                return true;
            }
            return false;
        }

        void banSufixes() {
            for (CountryRegion cr : regionCodeToCountryRegion.values()) {
                ArrayList<String> bannedSuffixes = new ArrayList<>();
                if (cr.regionCodeLength() == 1) {
                    for (CountryRegion cr2 : regionCodeToCountryRegion.values()) {
                        if (cr2.regionCodeLength() == 2) {
                            if (cr2.regionCodeBase32().startsWith(cr.regionCodeBase32())) {
                                bannedSuffixes.add(cr2.getRegionCode().substring(4));
                            }
                        }
                    }
                    for (CountryRegion cr3 : regionCodeToCountryRegion.values()) {
                        if (cr3.regionCodeLength() == 3) {
                            if (cr3.regionCodeBase32().startsWith(cr.regionCodeBase32())) {
                                // System.out.println("For " + cr.getRegionCode() + " Banning suffix " +
                                // cr3.getRegionCode().substring(4));
                                bannedSuffixes.add(cr3.getRegionCode().substring(4));
                            }
                        }
                    }
                } else if (cr.regionCodeLength() == 2) {
                    for (CountryRegion cr3 : regionCodeToCountryRegion.values()) {
                        if (cr3.regionCodeLength() == 3) {
                            if (cr3.regionCodeBase32().startsWith(cr.regionCodeBase32())) {
                                bannedSuffixes.add(cr3.getRegionCode().substring(5));
                            }
                        }
                    }
                }
                if (!bannedSuffixes.isEmpty()) {
                    cr.overlappedSuffixes(bannedSuffixes.toArray(CountryRegion.NO_STRINGS));
                }
            }
        }

    }
}
