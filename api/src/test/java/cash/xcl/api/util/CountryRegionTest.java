package cash.xcl.api.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CountryRegionTest {

    @Test
    public void creation() {
        String regionName = "Alba";
        String regionCode = "RO-AB";
        String countryCode3 = "ROU";
        String countryCode2 = "RO";
        String countryName = "Romania";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
        assertEquals(countryRegion.getCountryName(), countryName);
        assertEquals(countryRegion.getCountryCode2(), countryCode2);
        assertEquals(countryRegion.getCountryCode3(), countryCode3);
        assertEquals(countryRegion.getRegionName(), regionName);
        assertEquals(countryRegion.getRegionCode(), regionCode);
        assertEquals(countryRegion.getRegionCode(), countryRegion.toString());
        assertEquals(countryRegion.getRegionCodeBase32(), "r0ab");
    }

    @Test
    public void creationWithEmptySpaces() {
        String regionName = "Alba ";
        String regionCode = " RO-AB";
        String countryCode3 = " \nROU \t";
        String countryCode2 = "RO ";
        String countryName = " Romania ";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
        assertEquals(countryName.trim(), countryRegion.getCountryName());
        assertEquals(countryCode2.trim(), countryRegion.getCountryCode2());
        assertEquals(countryCode3.trim(), countryRegion.getCountryCode3());
        assertEquals(regionName.trim(), countryRegion.getRegionName());
        assertEquals(regionCode.trim(), countryRegion.getRegionCode());
        assertEquals(countryRegion.toString(), countryRegion.getRegionCode());
        assertEquals("r0ab", countryRegion.getRegionCodeBase32());
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongCountryCode3() {
        String regionName = "Alba";
        String regionCode = "RO-AB";
        String countryCode3 = "ROUM";
        String countryCode2 = "RO";
        String countryName = "Romania";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
        assertEquals(countryRegion.getCountryName(), countryName);
        assertEquals(countryRegion.getCountryCode2(), countryCode2);
        assertEquals(countryRegion.getCountryCode3(), countryCode3);
        assertEquals(countryRegion.getRegionName(), regionName);
        assertEquals(countryRegion.getRegionCode(), regionCode);
        assertEquals(countryRegion.getRegionCode(), countryRegion.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongCountryCode2() {
        String regionName = "Alba";
        String regionCode = "RO-AB";
        String countryCode3 = "ROU";
        String countryCode2 = "ROM";
        String countryName = "Romania";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
        assertEquals(countryRegion.getCountryName(), countryName);
        assertEquals(countryRegion.getCountryCode2(), countryCode2);
        assertEquals(countryRegion.getCountryCode3(), countryCode3);
        assertEquals(countryRegion.getRegionName(), regionName);
        assertEquals(countryRegion.getRegionCode(), regionCode);
        assertEquals(countryRegion.getRegionCode(), countryRegion.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shortRegionCode() {
        String regionName = "Alba";
        String regionCode = "RO";
        String countryCode3 = "ROU";
        String countryCode2 = "RO";
        String countryName = "Romania";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
        assertEquals(countryRegion.getCountryName(), countryName);
        assertEquals(countryRegion.getCountryCode2(), countryCode2);
        assertEquals(countryRegion.getCountryCode3(), countryCode3);
        assertEquals(countryRegion.getRegionName(), regionName);
        assertEquals(countryRegion.getRegionCode(), regionCode);
        assertEquals(countryRegion.getRegionCode(), countryRegion.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void longRegionCode() {
        String regionName = "Alba";
        String regionCode = "RO-ABCD";
        String countryCode3 = "ROU";
        String countryCode2 = "RO";
        String countryName = "Romania";
        CountryRegion countryRegion = new CountryRegion(countryName, countryCode2, countryCode3, regionCode, regionName);
    }
}
