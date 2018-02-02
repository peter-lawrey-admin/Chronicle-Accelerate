package cash.xcl.api.util;

import net.openhft.chronicle.bytes.BytesStore;

import java.util.HashMap;

public class AddressService {
    private final CountryRegionIndex countryRegionIndex;

    private final HashMap<Long, BytesStore<?, ?>> addresses = new HashMap<>();
    private final HashMap<String, RegionAddressGenerator> generator = new HashMap<>();

    public AddressService() {
        this(new CountryRegionIndex());
    }

    public AddressService(CountryRegionIndex countryRegionIndex) {
        this.countryRegionIndex = countryRegionIndex;
    }

    public long createAddress(String regionCode, BytesStore<?, ?> publicKey) {
        CountryRegion countryRegion = countryRegionIndex.getRegion(regionCode);
        if (countryRegion != null) {
            RegionAddressGenerator regionGenerator = generator.computeIfAbsent(regionCode, (k) -> new RegionAddressGenerator(countryRegion));
            long newAddress = regionGenerator.newAddress();
            while (addresses.containsKey(newAddress)) {
                newAddress = regionGenerator.newAddress();
            }
            return newAddress;
        } else {
            // return an error
            throw new IllegalArgumentException("Unknown region code " + regionCode); // probably some event will be sent
        }
    }

    public void save(long newAddress, BytesStore<?, ?> publicKey) {
        addresses.put(newAddress, publicKey);
    }

    public boolean hasAddress(long address) {
        return addresses.containsKey(address);
    }

    public CountryRegion getRegion(long address) {
        String encodedAddress = AddressUtil.encode(address);
        return countryRegionIndex.matchCountryRegion(encodedAddress);
    }

}
