package cash.xcl.api.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressUtilTest {
    @Test
    public void invalid() {
        assertEquals(~0, AddressUtil.INVALID_ADDRESS);
    }

}