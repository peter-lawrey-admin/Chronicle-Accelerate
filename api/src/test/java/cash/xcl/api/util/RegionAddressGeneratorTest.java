package cash.xcl.api.util;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

import static cash.xcl.api.util.AddressUtil.*;
import static org.junit.Assert.*;

public class RegionAddressGeneratorTest {

    static CountryRegionIndex regionIndex = new CountryRegionIndex();

    static RegionAddressGenerator ied = new RegionAddressGenerator(regionIndex.getRegion("IE-D"));
    static RegionAddressGenerator rohd = new RegionAddressGenerator(regionIndex.getRegion("RO-HD"));
    static RegionAddressGenerator gblnd = new RegionAddressGenerator(regionIndex.getRegion("GB-LND"));
    static RegionAddressGenerator[] generators = new RegionAddressGenerator[]{ied, rohd, gblnd};

    @Test
    public void testRegionPrefix() {
        assertEquals("r0hd", rohd.getRegionPrefix());
        assertEquals("gb1nd", gblnd.getRegionPrefix());
        assertEquals("ied", ied.getRegionPrefix());
    }

    @Test
    public void testBasic() {
        for (RegionAddressGenerator generator : generators) {
            for (int i = 0; i < 37; i++) {
                long address = generator.newAddress();
                assertTrue(encode(address).startsWith(generator.getRegionPrefix()));
                assertTrue(isValid(address));
                assertFalse(isReserved(address));
                assertEquals(decode(encode(address)), address);
                for (RegionAddressGenerator generator2 : generators) {
                    if (generator == generator2) {
                        assertTrue(generator2.isAddresFromRegion(address));
                    } else {
                        assertFalse(generator2.isAddresFromRegion(address));
                    }
                }
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeValue() {
        for (RegionAddressGenerator generator : generators) {
            generator.newAddressFrom(-10000L);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooLargeValueValue() {
        ied.newAddressFrom(ied.getMaxAddress() + 1);
    }

    @Test
    public void testMaxAddressValue() {
        long addressIe = ied.newAddressFrom(ied.getMaxAddress());
        assertEquals("iedg00000000n", encode(addressIe));

        long addressRo = rohd.newAddressFrom(rohd.getMaxAddress());
        assertEquals("r0hdg0000001t", encode(addressRo));

        long addressGb = gblnd.newAddressFrom(gblnd.getMaxAddress());
        assertEquals("gb1ndg0000024", encode(addressGb));
    }

    @Test
    public void testMinAddressValue() {
        long addressIe = ied.newAddressFrom(0);
        assertTrue(addressIe == AddressUtil.INVALID_ADDRESS); // Overlaps IE-CO region

        long addressRo = rohd.newAddressFrom(0);
        assertEquals("r0hd00000000i", encode(addressRo));

        long addressGb = gblnd.newAddressFrom(0);
        assertEquals("gb1nd00000002", encode(addressGb));

    }

    @Test
    public void testIsValid() {
        for (int i = 0; i < 10; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            long randAddress = random.nextLong();
            randAddress -= randAddress % CHECK_NUMBER;
            assertTrue(isValid(randAddress));
            assertFalse(isValid(randAddress + 23));
        }
    }

    @Test
    public void testIsReserve() {
        for (int i = 0; i < 10; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            long randAddress = random.nextLong();
            randAddress = randAddress / 16 * 16;
            assertTrue(isReserved(randAddress));
            assertFalse(isReserved(randAddress + 1));
        }
    }

    @Test
    public void overlappingAddreses() {
        checkOverlapping("RO-B");
        checkOverlapping("IE-D");
        checkOverlapping("ES-A");
    }

    private void checkOverlapping(String regionCode) {
        CountryRegion region = regionIndex.getRegion(regionCode);
        RegionAddressGenerator roB = new RegionAddressGenerator(region);
        String[] overallpingRegions = region.overlappedSuffixes();
        for (String overLaping : overallpingRegions) {
            CountryRegion oRegion = regionIndex.getRegion(regionCode + overLaping);
            RegionAddressGenerator roReg = new RegionAddressGenerator(oRegion);
            for (int i = 0; i < 100; i++) {
                long bAddress = roB.newAddress();
                long boAddress = roReg.newAddress();
                assertTrue(roReg.isAddresFromRegion(boAddress));
                assertFalse(roB.isAddresFromRegion(boAddress));
                assertTrue(roB.isAddresFromRegion(bAddress));
                assertFalse(roReg.isAddresFromRegion(bAddress));
            }
        }
    }

    /**
     * This is the mother of all tests. For every region we should be able to generate valid addresses If it takes to long you can skipped
     */
    @Test
    public void checkAll() {
        for (CountryRegion cr : regionIndex.countryRegions()) {
            for (int i = 0; i < 20; i++) {
                RegionAddressGenerator generator = new RegionAddressGenerator(cr);
                long address = generator.newAddress();
                assertTrue(encode(address).startsWith(generator.getRegionPrefix()));
                assertTrue(isValid(address));
                assertFalse(isReserved(address));
                assertEquals(decode(encode(address)), address);
            }
        }
    }

    @Test
    public void checkAllBase32() {
        for (CountryRegion cr : regionIndex.countryRegions()) {
            RegionAddressGenerator generator = new RegionAddressGenerator(cr);
            long address = generator.newAddress();
            String encodedAddress = AddressUtil.encode(address);
            assertEquals(regionIndex.matchCountryRegion(encodedAddress), cr);
        }
    }
}
