package cash.xcl.api.util;

import cash.xcl.util.UnsignedLong;
import cash.xcl.util.XCLBase32;

import java.util.concurrent.ThreadLocalRandom;

import static cash.xcl.api.util.AddressUtil.*;

public class RegionAddressGenerator {

    final long[] bannedSuffixes;
    private final String regionCode;
    private final long maxAddress;
    private final long regionPrefix;
    private final byte shiftCount;
    private final long shiftedRegionPrefix;
    private final byte[] shiftCountsForBanned;

    public RegionAddressGenerator(CountryRegion region) {
        this.regionCode = XCLBase32.normalize(region.getRegionCode());
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
        this.maxAddress = 1L << shiftCount;
    }

    long newAddressFrom(long seed) {
        long address = seed & (maxAddress - 1);
        long regionStart = (regionPrefix & ~(maxAddress - 1));
        long newAddress = regionStart + address;
        newAddress -= UnsignedLong.mod(newAddress, CHECK_NUMBER);
        if (newAddress < regionStart)
            newAddress += CHECK_NUMBER;

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
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long address;
        do {
            address = newAddressFrom(random.nextLong(maxAddress));
        } while (address == INVALID_ADDRESS);
        return address;
    }

    public boolean isAddressFromRegion(long address) {
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