package cash.xcl.api.util;

import net.openhft.chronicle.bytes.Bytes;

import java.util.Arrays;

/*
 * seer rfc/XCLBase32.adoc for more details.
 */

public enum XCLBase32 {
    ;
    public static final int BITS_PER_CHAR = 5;
    static final byte[] PARSING = new byte[128];
    static final char[] ENCODING = "0123456789abcdefghijkmnprstuvwyz".toCharArray();

    static {
        Arrays.fill(PARSING, (byte) -1);
        byte i = 0;
        for (char ch : ENCODING) {
            setParsing(i++, ch);
        }
        setParsing((byte) 0, 'o');
        setParsing((byte) 1, 'l');
        setParsing((byte) 8, 'x');
        setParsing((byte) 9, 'q');
    }

    private static void setParsing(byte i, char ch) {
        PARSING[ch] = i;
        PARSING[Character.toUpperCase(ch)] = i;
    }

    public static void encode(Bytes<?> bytes, long value) {
        do {
            int digit = (int) (value >>> 59);
            bytes.append(ENCODING[digit]);
            value <<= 5;
        } while (value != 0);
    }

    public static long decode(Bytes<?> bytes) {
        long n = 0;
        int shift = Long.SIZE - BITS_PER_CHAR;
        do {
            int ch = bytes.readUnsignedByte();
            if (ch < 0) {
                return n;
            }
            long value = ch < PARSING.length ? PARSING[ch] : -1;
            if (value < 0) {
                return n;
            }
            n |= shift < 0 ? value >> -shift : value << shift;
            shift -= BITS_PER_CHAR;
        } while (shift >= -1);
        int ch = bytes.readUnsignedByte();
        if (ch >= 0) {
            if (ch < PARSING.length && PARSING[ch] >= 0) {
                throw new IllegalArgumentException("Encoded number too long at " + (char) ch);
            }
        }
        return n;
    }

    public static String normalize(String regionCode) {
        StringBuilder sb = new StringBuilder(regionCode.length());
        for (int i = 0; i < regionCode.length(); i++) {
            char ch = Character.toLowerCase(regionCode.charAt(i));
            switch (ch) {
                case 'o':
                    ch = '0';
                    break;
                case 'l':
                    ch = '1';
                    break;
                case 'x':
                    ch = '8';
                    break;
                case 'q':
                    ch = '9';
                    break;
                case '-':
                    continue;
            }
            sb.append(ch);
        }

        return sb.toString();
    }
}
