package cash.xcl.api.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CountryRegionIndexTest {
    static CountryRegionIndex index = new CountryRegionIndex();

    @Test
    public void loading() {
        assertTrue(index.isValidRegion("GB-LND"));
        assertTrue(index.isValidRegion("IE-D"));
        assertTrue(index.isValidRegion("RO-AB"));
        assertTrue(index.isValidRegion("RO-HD"));
        assertFalse(index.isValidRegion("RO-HDD"));
        assertFalse(index.isValidRegion("askfdaf"));
        assertFalse(index.isValidRegion(null));
    }

    @Test
    public void bannedSuffixes() {
/*
        Set<String> conflicts = new TreeSet<>(Arrays.asList((
                "EC-S, EG-MN, EG-SU, ES-A, ES-B, ES-C, ES-H, ES-L, ES-M, ES-O, ES-P, ES-S, ES-T, ES-V, ES-Z, GR-L, IE-D, RO-B, RU-AL, RU-KL, RU-KO, RU-KR, RU-MO, RU-SA, RU-TA, RU-TY").split(", ")));
        for (String conflict : conflicts) {
            CountryRegion region = index.getRegion(conflict);
            if (region == null) continue;
            String[] overlappedSuffixes = region.getOverlappedSuffixes();
            if (overlappedSuffixes == null)
                continue;
            System.out.println("assertEquals(\""+Arrays.toString(overlappedSuffixes)+"\", Arrays.toString(index.getRegion(\""+conflict+"\").getOverlappedSuffixes()));");
        }
        System.out.println(conflicts);
*/

        assertTrue(index.getRegion("GB-LND").getOverlappedSuffixes().length == 0);
        assertTrue(index.getRegion("RO-AB").getOverlappedSuffixes().length == 0);
        assertEquals("[d, e]", Arrays.toString(index.getRegion("EC-S").getOverlappedSuffixes()));
        assertEquals("[f]", Arrays.toString(index.getRegion("EG-MN").getOverlappedSuffixes()));
        assertEquals("[z]", Arrays.toString(index.getRegion("EG-SU").getOverlappedSuffixes()));
        assertEquals("[3, 7]", Arrays.toString(index.getRegion("GR-L").getOverlappedSuffixes()));
        assertEquals("[1]", Arrays.toString(index.getRegion("IE-D").getOverlappedSuffixes()));
        assertEquals("[c, h, n, r, t, v, z]", Arrays.toString(index.getRegion("RO-B").getOverlappedSuffixes()));
        assertEquals("[t]", Arrays.toString(index.getRegion("RU-AL").getOverlappedSuffixes()));
        assertEquals("[u]", Arrays.toString(index.getRegion("RU-KL").getOverlappedSuffixes()));
        assertEquals("[s]", Arrays.toString(index.getRegion("RU-KO").getOverlappedSuffixes()));
        assertEquals("[s]", Arrays.toString(index.getRegion("RU-KR").getOverlappedSuffixes()));
        assertEquals("[s, w]", Arrays.toString(index.getRegion("RU-MO").getOverlappedSuffixes()));
        assertEquals("[k, m, r]", Arrays.toString(index.getRegion("RU-SA").getOverlappedSuffixes()));
        assertEquals("[m]", Arrays.toString(index.getRegion("RU-TA").getOverlappedSuffixes()));
        assertEquals("[u]", Arrays.toString(index.getRegion("RU-TY").getOverlappedSuffixes()));
    }

    @Test
    public void findBase32() {
        assertEquals(index.getFromBase32("gb1nd"), index.getRegion("GB-LND"));
        assertEquals(index.getFromBase32("r0ab"), index.getRegion("RO-AB"));
        assertEquals(index.getFromBase32("r0b"), index.getRegion("RO-B"));
        assertEquals(index.getFromBase32("r0bc"), index.getRegion("RO-BC"));
        assertEquals(index.getFromBase32("ied"), index.getRegion("IE-D"));
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
        assertEquals(index.matchCountryRegion("ied"), index.getRegion("IE-D"));
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
