package cash.xcl.api.util;

import org.junit.Test;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import static cash.xcl.api.util.AddressUtil.*;
import static org.junit.Assert.*;

public class RegionAddressGeneratorTest {

    static CountryRegionIndex regionIndex = new CountryRegionIndex();

    static RegionAddressGenerator ied = new RegionAddressGenerator(regionIndex.getRegion("IE-D"));
    static RegionAddressGenerator rob = new RegionAddressGenerator(regionIndex.getRegion("RO-B"));
    static RegionAddressGenerator rohd = new RegionAddressGenerator(regionIndex.getRegion("RO-HD"));
    static RegionAddressGenerator gblnd = new RegionAddressGenerator(regionIndex.getRegion("GB-LND"));
    static RegionAddressGenerator[] generators = new RegionAddressGenerator[]{ied, rohd, gblnd};

    @Test
    public void testRegionPrefix() {
        assertEquals("rohd", rohd.getRegionPrefix());
        assertEquals("gblnd", gblnd.getRegionPrefix());
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
                        assertTrue(generator2.isAddressFromRegion(address));
                    } else {
                        assertFalse(generator2.isAddressFromRegion(address));
                    }
                }
            }
        }
    }

    @Test
    public void testMaxAddressValue() {
        long addressIe = ied.newAddressFrom(ied.getMaxAddress() - 1);
        assertEquals("ied........ya", encode(addressIe));

        long addressRo = rohd.newAddressFrom(rohd.getMaxAddress() - 1);
        assertEquals("rohd.......wn", encode(addressRo));

        long addressGb = gblnd.newAddressFrom(gblnd.getMaxAddress() - 1);
        assertEquals("gblnd......yt", encode(addressGb));
    }

    @Test
    public void testOverlapping() {
//        long addressIe = ied.newAddressFrom(decode("100000001i"));
//        assertEquals(0, addressIe);

        for (long bannedSuffix : rob.bannedSuffixes) {
            long addressRob = rob.newAddressFrom((bannedSuffix << -20) + 100);
            assertEquals(".", encode(addressRob));

        }
    }

    @Test
    public void testMinAddressValue() {
        long addressIe = ied.newAddressFrom(0);
        assertEquals("iedooooooooli", encode(addressIe));

        long addressRo = rohd.newAddressFrom(0);
        assertEquals("rohdooooooo24", encode(addressRo));

        long addressGb = gblnd.newAddressFrom(0);
        assertEquals("gblndoooooolk", encode(addressGb));

    }

    @Test
    public void testIsValid() {
        long maxValue = BigInteger.valueOf(1).shiftLeft(64).divide(BigInteger.valueOf(37)).longValue();
        for (int i = 0; i < 35; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            long randAddress = Math.abs(random.nextLong() % maxValue) * 37;
            assertTrue(isValid(randAddress));
            assertFalse(isValid(randAddress + i + 1));
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
        checkOverlapping("RU-SA");
        checkOverlapping("GR-L");

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
                assertTrue(roReg.isAddressFromRegion(boAddress));
                assertFalse(roB.isAddressFromRegion(boAddress));
                assertTrue(roB.isAddressFromRegion(bAddress));
                assertFalse(roReg.isAddressFromRegion(bAddress));
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
            assertEquals(cr, regionIndex.matchCountryRegion(encodedAddress));
        }
    }
}
