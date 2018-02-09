package cash.xcl.api.util;

import cash.xcl.api.dto.CurrencyInfo;
import net.openhft.chronicle.bytes.BytesUtil;
import net.openhft.chronicle.core.util.ObjectUtils;
import net.openhft.chronicle.wire.CSVWire;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class CurrencyToRegionMain {
    public static void main(String[] args) throws IOException {
        CSVWire wire = new CSVWire(BytesUtil.readFile("cash/xcl/api/util/currencies.csv"));
        Set<String> regions = new TreeSet<>();
        while (true) {
            wire.consumeWhiteSpace();
            if (wire.isEmpty())
                break;

            CurrencyInfo ci = ObjectUtils.newInstance(CurrencyInfo.class);
            ci.readMarshallable(wire);

            String s = String.format("%s, %s, %s, \"\", \"\"",
                    "Exchange",
                    "2-" + ci.currency(),
                    ci.description()
            );
            regions.add(s);
        }
        regions.forEach(System.out::println);
    }
}
