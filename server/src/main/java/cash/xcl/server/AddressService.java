package cash.xcl.server;

import cash.xcl.api.util.AddressUtil;
import cash.xcl.api.util.CountryRegion;
import cash.xcl.api.util.CountryRegionIndex;
import cash.xcl.api.util.RegionAddressGenerator;
import net.openhft.chronicle.bytes.BytesStore;

import java.util.LinkedHashMap;
import java.util.Map;

public class AddressService {
    private final CountryRegionIndex countryRegionIndex;

    private final Map<Long, BytesStore<?, ?>> addresses = new LinkedHashMap<>();
    private final Map<CountryRegion, RegionAddressGenerator> generator = new LinkedHashMap<>();

    public AddressService() {
        this(new CountryRegionIndex());
    }

    public AddressService(CountryRegionIndex countryRegionIndex) {
        this.countryRegionIndex = countryRegionIndex;
    }

    public void save(long newAddress, BytesStore<?, ?> publicKey) {
        addresses.put(newAddress, publicKey.copy());
    }

    public boolean hasAddress(long address) {
        return addresses.containsKey(address);
    }

    public CountryRegion getRegion(long address) {
        String encodedAddress = AddressUtil.encode(address);
        return countryRegionIndex.matchCountryRegion(encodedAddress);
    }

    public void addAddress(long newAddress, BytesStore<?, ?> publicKey) throws IllegalArgumentException {
        BytesStore<?, ?> bytesStore = addresses.get(newAddress);
        if (bytesStore == null)
            addresses.put(newAddress, publicKey.copy());
        else if (!bytesStore.equals(publicKey))
            throw new IllegalArgumentException("Address " + addresses + " already taken");
    }

    public long createAddress(String regionCode, BytesStore<?, ?> publicKey) throws IllegalArgumentException {
        long newAddress = generateAddress(regionCode);
        addresses.put(newAddress, publicKey.copy());
        return newAddress;
    }

    public long generateAddress(String regionCode) {
        CountryRegion countryRegion = countryRegionIndex.getRegion(regionCode);
        if (countryRegion != null) {
            RegionAddressGenerator regionGenerator = generator.computeIfAbsent(countryRegion, RegionAddressGenerator::new);
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
}
