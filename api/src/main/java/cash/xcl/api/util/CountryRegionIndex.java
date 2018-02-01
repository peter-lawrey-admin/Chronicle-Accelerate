package cash.xcl.api.util;

import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.wire.CSVWire;

import java.io.IOException;
import java.util.HashMap;

public class CountryRegionIndex {
    private final HashMap<String, CountryRegion> index = new HashMap<>();

    public CountryRegionIndex() {
        this("cash/xcl/api/util/country_state_codes.csv");
    }

    public CountryRegionIndex(String resourceName) {
        try {
            CSVWire wire = new CSVWire(BytesUtil.readFile(resourceName));
            while (true) {
                wire.consumeWhiteSpace();
                if (wire.isEmpty())
                    break;

                CountryRegion cr = ObjectUtils.newInstance(CountryRegion.class);
                cr.readMarshallable(wire);
//                System.out.println(cr);
                index.put(cr.regionCode(), cr);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public boolean isValidRegion(String regionCode) {
        return index.containsKey(regionCode);
    }

    public CountryRegion getRegion(String regionCode) {
        return index.get(regionCode);
    }

}
