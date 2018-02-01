package cash.xcl.api.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CountryRegionIndexTest {
    static CountryRegionIndex index = new CountryRegionIndex();

    @Test
    public void loading() {
        assertTrue(index.isValidRegion("GB-LND"));
        assertTrue(index.isValidRegion("IE-C"));
        assertTrue(index.isValidRegion("RO-AB"));
        assertTrue(index.isValidRegion("RO-HD"));
        assertFalse(index.isValidRegion("RO-HDD"));
        assertFalse(index.isValidRegion("askfdaf"));
        assertFalse(index.isValidRegion(null));
    }

    @Test
    public void bannedSuffixes() {
        assertTrue(index.getRegion("GB-LND").getOverlappedSuffixes().length == 0);
        assertTrue(index.getRegion("RO-AB").getOverlappedSuffixes().length == 0);
        assertArrayEquals(index.getRegion("IE-C").getOverlappedSuffixes(), new String[]{"0", "e", "n", "w"});
        assertArrayEquals(index.getRegion("MH-L").getOverlappedSuffixes(), new String[]{"ae", "ib", "ik"});
        assertEquals("[c, h, n, r, t, v, z]", Arrays.toString(index.getRegion("RO-B").getOverlappedSuffixes()));
        assertArrayEquals(index.getRegion("FR-L").getOverlappedSuffixes(), new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
    }

    @Test
    public void findBase32() {
        assertEquals(index.getFromBase32("gb1nd"), index.getRegion("GB-LND"));
        assertEquals(index.getFromBase32("r0ab"), index.getRegion("RO-AB"));
        assertEquals(index.getFromBase32("r0b"), index.getRegion("RO-B"));
        assertEquals(index.getFromBase32("r0bc"), index.getRegion("RO-BC"));
        assertEquals(index.getFromBase32("iec"), index.getRegion("IE-C"));
    }

    @Test
    public void matchBase32() {
        // index.print();
        assertEquals(index.matchCountryRegion("gb1nd"), index.getRegion("GB-LND"));
        assertEquals(index.matchCountryRegion("r0ab"), index.getRegion("RO-AB"));
        assertEquals(index.matchCountryRegion("r0b"), index.getRegion("RO-B"));
        assertEquals(index.matchCountryRegion("r0bc"), index.getRegion("RO-BC"));
        assertEquals(index.matchCountryRegion("r0bc323"), index.getRegion("RO-BC"));
        assertEquals(index.matchCountryRegion("r0b1323"), index.getRegion("RO-B"));
        assertEquals(index.matchCountryRegion("iec"), index.getRegion("IE-C"));
        assertEquals(index.matchCountryRegion("iec0"), index.getRegion("IE-CO"));
        assertEquals(index.matchCountryRegion("iec01"), index.getRegion("IE-CO"));
        assertNull(index.matchCountryRegion("r0ad"));
        assertNull(index.matchCountryRegion("r0ad112"));
    }

/*
    @Test(expected = IllegalArgumentException.class)
    public void parseRecords5() {
        assertNull(CountryRegionIndex.parseRecords(new String[] { "Albania", "AL-BR", "Berat", "ALBB", "AL" }, false));
    }

    @Test
    public void parseResourceWithSomeErr1() {
        CountryRegionIndex crIndex = new CountryRegionIndex("country_state_codes-few-errors.csv", true);
        AtomicInteger counter = new AtomicInteger(10);
        crIndex.forEach((cr) -> counter.decrementAndGet());
        assertEquals(counter.get(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseResourceWithSomeErr2() {
        new CountryRegionIndex("country_state_codes-few-errors.csv", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseResourceWithDuplicateLine1() {
        new CountryRegionIndex("country_state_codes-with-duplicate-line.csv", false);
    }

    @Test
    public void parseResourceWithDuplicateLine2() {
        CountryRegionIndex crIndex = new CountryRegionIndex("country_state_codes-with-duplicate-line.csv", true);
        assertNotNull(crIndex.getRegion("AF-BGL"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseResourceRegionsWithSameCode() {
        new CountryRegionIndex("country_state_codes-regions-same-code.csv", true);
    }
*/
}
