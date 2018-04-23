package cash.xcl.api.util;

import cash.xcl.util.UnsignedLong;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"NumericOverflow", "ConstantOverflow"})
public class UnsignedLongTest {

    @Test
    public void compare() {
        assertEquals(+1, UnsignedLong.compare(Long.MAX_VALUE + 100, Long.MAX_VALUE + 10));
        assertEquals(-1, UnsignedLong.compare(Long.MAX_VALUE - 100, Long.MAX_VALUE + 10));
    }

    @Test
    public void mod() {
        assertEquals(17, UnsignedLong.mod(Long.MAX_VALUE / 32 * 32 * 2 + 17, 32));
        assertEquals(17, UnsignedLong.mod(Long.MAX_VALUE / 37 * 37 * 2 - 37 + 17, 37));

        for (int mod = 2; mod < 50; mod++) {
            for (int i = 0; i < mod; i++) {
//                System.out.println(mod + " & " + i);
                assertEquals(i, UnsignedLong.mod((Long.MAX_VALUE / mod - 1) * mod + i, mod));
                assertEquals(i, UnsignedLong.mod((Long.MAX_VALUE / mod - 1) * mod * 2 + i, mod));
            }

            if (mod > 2) mod++;
        }
    }
}