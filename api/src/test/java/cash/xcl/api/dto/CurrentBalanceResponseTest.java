package cash.xcl.api.dto;

import cash.xcl.util.XCLBase32;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class CurrentBalanceResponseTest {
    @Test
    public void testToString() {
        CurrentBalanceResponse cbr = new CurrentBalanceResponse(
                XCLBase32.decode("test.server"),
                12345,
                XCLBase32.decode("dst.addr"),
                Collections.emptyMap()
        );
        assertEquals("!CurrentBalanceResponse {\n" +
                "  sourceAddress: test.server,\n" +
                "  eventTime: 12345,\n" +
                "  address: dst.addr,\n" +
                "  balances: {\n" +
                "  }\n" +
                "}\n", cbr.toString());
    }

}