package cash.xcl.server.frame;

import cash.xcl.api.AllMessages;
import cash.xcl.api.util.CountryRegion;
import net.openhft.chronicle.wire.TextMethodTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegionalFrameTest {
    public static void test(String basename) {
        CountryRegion region = new CountryRegion("ZZ Test", "ZZ", "ZZZ", "ZZ-TST", "ZZ Test");
        TextMethodTester<AllMessages> tester = new TextMethodTester<>(basename + "/in.yaml",
                out -> new RegionalFrame(region, a -> out),
                AllMessages.class, basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        assertEquals(tester.expected(), tester.actual());
    }

    @Test
    public void nothing() {
        test("nothing");
    }
}
