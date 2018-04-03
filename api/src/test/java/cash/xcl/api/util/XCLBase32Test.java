package cash.xcl.api.util;

import net.openhft.chronicle.bytes.Bytes;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static cash.xcl.api.util.XCLBase32.decode;
import static cash.xcl.api.util.XCLBase32.encode;
import static org.junit.Assert.assertEquals;

public class XCLBase32Test {

    private static final ThreadLocal<Bytes<?>> bytesCache = ThreadLocal.withInitial(() -> Bytes.elasticByteBuffer(32));

    static void doTest(long l) {
        Bytes<?> bytes = bytesCache.get();
        bytes.zeroOut(0, bytes.realCapacity());
        bytes.clear();
        encode(bytes, l);
        bytes.append(' ');
        encode(bytes, -l);
        bytes.append(' ');
        assertEquals(l, decode(bytes));
        assertEquals(-l, decode(bytes));
    }

    @Test
    public void decode1() {
        Random rand = new Random();
        for (int i = 0; i < 1000; i++) {
            doTest(i);
            doTest(rand.nextLong());
        }
    }

    @Test
    public void decode2() {
        doTest2("de6defffffffffff", "usny");
        doTest2("82c2db7fffffffff", "gbldn");
        doTest2("65823fffffffffff", "cn13");
        doTest2("4076ca7bffffffff", "8lucky");
        doTest2("e01bf0ffffffffff", "vod.l");
        doTest2("76f1f4323fffffff", "eur.xch");
        doTest2("bbb4ec055dc3bdff", "peterlawrey");
        doTest2("bbb4ec7c2aee1def", "peter.lawrey");
    }

    @Test
    public void logic() {
        assertEquals("ooooooo", XCLBase32.encodeInt(0));
        assertEquals("ooooooe", XCLBase32.encodeInt(1));
        assertEquals("ooooooi", XCLBase32.encodeInt(2));
        assertEquals("oooooo", XCLBase32.encodeInt(3));
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            int v = random.nextInt();
            String encode = XCLBase32.encodeInt(v);
            assertEquals(v, XCLBase32.decodeInt(encode));
            assertEquals(v, XCLBase32.decodeInt(XCLBase32.encodeIntNum(v)));
            assertEquals(v, XCLBase32.decodeInt(XCLBase32.encodeIntNum(v)));
        }
        for (int i = 0; i < 1000; i++) {
            long v = random.nextLong();
            String encode = XCLBase32.encode(v);
            assertEquals(v, XCLBase32.decode(encode));
        }
        long _0 = XCLBase32.decode("0");
        long _00 = XCLBase32.decode("00");
        assertEquals((1L << 59) - 1, _0);
        assertEquals((1L << 54) - 1, _00);
        assertEquals("o", XCLBase32.encode(_0));
        long abcd = XCLBase32.decode("abcd");
        long two = XCLBase32.decode("abce000000000");
        assertEquals(1, two - abcd);
        assertEquals("abcd", XCLBase32.encode(abcd));
        assertEquals("abcd........v", XCLBase32.encode(abcd - 1));
        assertEquals("abceooooooooo", XCLBase32.encode(abcd + 1));
    }

    private void doTest2(String expected, String input) {
        assertEquals(expected, Long.toHexString(decode(input)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toLong() {
        Bytes<?> bytes = bytesCache.get();
        bytes.zeroOut(0, bytes.realCapacity());
        bytes.clear();
        bytes.write("1234567890123456"); // more than 13 chars
        decode(bytes);
    }

    @Test
    public void roundtrip() {
        Bytes<?> bytes = bytesCache.get();
        Random rand = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            bytes.zeroOut(0, bytes.realCapacity());
            bytes.clear();
            long address = rand.nextLong();
            encode(bytes, address);
            assertEquals(decode(bytes), address);
        }

    }

    @Test
    public void encodeMultipleUnicodeBetween() {
        Bytes<?> bytes = bytesCache.get();
        bytes.zeroOut(0, bytes.realCapacity());
        bytes.clear();
        encode(bytes, 0X12345678L);
        bytes.append('\u0218');
        encode(bytes, 0X12345678L);
        decode(bytes);
        decode(bytes);
    }

    @Test
    public void decodeUnicodeIn() {
        long val1 = decode(Bytes.from("asdf\u0219123"));
        long val2 = decode(Bytes.from("asdf"));
        assertEquals(val1, val2);

        val1 = decode(Bytes.from("asdf\u00ff123"));
        val2 = decode(Bytes.from("asdf"));
        assertEquals(val1, val2);
    }

    @Test
    public void decodeUnicodeIn2() {
        long val1 = decode("asdf\u0219123");
        long val2 = decode(Bytes.from("asdf"));
        assertEquals(val1, val2);

        val1 = decode(Bytes.from("asdf\u00ff123"));
        val2 = decode("asdf");
        assertEquals(val1, val2);
    }

    @Test
    public void decodeZeroAtEnd() {
        long val = 0x1234567812345678L;
        Bytes<?> bytes = bytesCache.get();
        bytes.zeroOut(0, bytes.realCapacity());
        bytes.clear();
        encode(bytes, val);
        bytes.writeByte((byte) 0);
        assertEquals(val, decode(bytes));
    }
}