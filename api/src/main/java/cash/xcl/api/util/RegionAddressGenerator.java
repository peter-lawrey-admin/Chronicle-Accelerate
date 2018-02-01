package cash.xcl.api.util;

import java.util.concurrent.ThreadLocalRandom;

import static cash.xcl.api.util.AddressUtil.*;


public class RegionAddressGenerator {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final String regionCode;
    private final long maxAddress;
    private final long regionPrefix;

    private final byte shiftCount;
    private final long shiftedRegionPrefix;

    private final byte[] shiftCountsForBanned;
    private final long[] bannedSuffixes;

    RegionAddressGenerator(CountryRegion region) {
        this.regionCode = region.getRegionCode().replace("-", "");
        this.regionPrefix = decode(regionCode);
        this.shiftCount = shiftCountFor(regionCode.length());
        this.shiftedRegionPrefix = regionPrefix >>> shiftCount;

        String[] bannedSufixes = region.getOverlappedSuffixes();
        bannedSuffixes = new long[bannedSufixes.length];
        shiftCountsForBanned = new byte[bannedSufixes.length];
        for (int i = 0; i < bannedSufixes.length; i++) {
            String suffix = bannedSufixes[i];
            String bannedAddressSufix = regionCode + suffix;
            shiftCountsForBanned[i] = shiftCountFor(bannedAddressSufix.length());
            bannedSuffixes[i] = decode(bannedAddressSufix) >>> shiftCountsForBanned[i];
        }
        int bitsForRegion = regionCode.length() * 5;
        int maxAddressBits = Long.SIZE - 1 - bitsForRegion;
        this.maxAddress = 1L << maxAddressBits;
    }

    long newAddressFrom(long value) {
        if (value > maxAddress) {
            throw new IllegalArgumentException();
        }
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        long newAddress = regionPrefix | value;
        newAddress -= newAddress % CHECK_NUMBER;
        if (isValid(newAddress) && !isReserved(newAddress) && doesNotOverlap(newAddress)) {
            return newAddress;
        } else {
            return INVALID_ADDRESS;
        }
    }

    private boolean doesNotOverlap(long address) {
        for (int i = 0; i < bannedSuffixes.length; i++) {
            if ((address >>> shiftCountsForBanned[i]) == bannedSuffixes[i]) {
                return false;
            }
        }
        return true;
    }

    public long newAddress() {
        long address = newAddressFrom(random.nextLong(maxAddress));
        while (address == INVALID_ADDRESS) {
            address = newAddressFrom(random.nextLong(maxAddress));
        }
        return address;
    }

    public boolean isAddresFromRegion(long address) {
        return ((address >>> shiftCount) == shiftedRegionPrefix) && doesNotOverlap(address);
    }

    String getRegionPrefix() {
        return encode(regionPrefix);
    }

    long getMaxAddress() {
        return maxAddress;
    }

    byte shiftCountFor(int codeLenInChars) {
        assert (codeLenInChars * 5) < Long.SIZE;
        return (byte) (Long.SIZE - (codeLenInChars * 5));
    }
}