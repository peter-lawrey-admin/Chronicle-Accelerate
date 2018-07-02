package cash.xcl.util;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesOut;

import java.util.Arrays;

/*
 * seer rfc/XCLBase32.adoc for more details.
 */

public enum XCLBase32 {
    ;
    public static final int BITS_PER_CHAR = 5;
    static final byte[] PARSING = new byte[128];
    public static final String STR2 = "ol23456zxqabcdefghijkmnprstuvwy.";
    static final char[] ENCODING_NUM = "0123456789abcdefghijkmnprstuvwy.".toCharArray();
    static final char[] ENCODING_STR = STR2.toCharArray();
    static final char[] ENCODING_UPPER = STR2.toUpperCase().toCharArray();

    static {
        Arrays.fill(PARSING, (byte) -1);
        byte i = 0;
        for (char ch : ENCODING_NUM)
            setParsing(i++, ch);
        i = 0;
        for (char ch : ENCODING_STR)
            setParsing(i++, ch);
    }

    private static void setParsing(byte i, char ch) {
        PARSING[ch] = (byte) (31 - i);
        PARSING[Character.toUpperCase(ch)] = (byte) (31 - i);
    }

    public static String encodeInt(int value) {
        return encodeIntWith(value, ENCODING_STR);
    }

    public static String encodeIntNum(int value) {
        return encodeIntWith(value, ENCODING_STR);
    }

    public static String encodeIntUpper(int value) {
        return encodeIntWith(value, ENCODING_UPPER);
    }

    public static String encodeIntWith(int value, char[] encoding) {
        int lowest = value & 3;
        int length = 7;
        if (lowest == 3) {
            length--;
            long v2 = value >>> 2;
            while (length > 1 && (v2 & 31) == 31) {
                length--;
                v2 >>>= 5;
            }
        }
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int digit = value >>> 27;
            if (i == 6) {
                if (digit == 8)
                    digit = 14;
                else if (digit == 16)
                    digit = 18;
            }
            chars[i] = encoding[digit];
            value <<= 5;
        }
        return new String(chars);
    }

    public static String encode(long value) {
        return encodeWith(value, ENCODING_STR);
    }

    public static String encodeWith(long value, char[] encoding) {
        int lowest = (int) (value & 15);
        int length = 13;
        if (lowest == 15) {
            length--;
            long v2 = value >>> 4;
            while (length > 1 && (v2 & 31) == 31) {
                length--;
                v2 >>>= 5;
            }
        }
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            int digit = (int) (value >>> 59);
            chars[i] = encoding[digit];
            value <<= 5;
        }
        return new String(chars);
    }

    public static void encode(BytesOut<?> bytes, long value) {
         encodeWith(bytes, value, ENCODING_STR);
    }

    public static void encodeWith(BytesOut<?> bytes, long value, char[] encoding) {
        int lowest = (int) (value & 15);
        int length = 13;
        if (lowest == 15) {
            length--;
            long v2 = value >>> 4;
            while (length > 1 && (v2 & 31) == 31) {
                length--;
                v2 >>>= 5;
            }
        }
        for (int i = 0; i < length; i++) {
            int digit = (int) (value >>> 59);
            bytes.append(encoding[digit]);
            value <<= 5;
        }
    }

    public static long decode(Bytes<?> bytes) {
        long n = 0;
        int shift = Long.SIZE - BITS_PER_CHAR;
        do {
            int ch = bytes.readUnsignedByte();
            if (ch == '-') continue;
            if (ch < 0) {
                return ~n;
            }
            long value = ch < PARSING.length ? PARSING[ch] : -1;
            if (value < 0) {
                return ~n;
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
        return ~n;
    }

    public static int decodeInt(CharSequence chars) {
        return (int) (decode(chars) >> 32);
    }

    public static long decode(CharSequence chars) {
        if (chars instanceof Bytes)
            return decode((Bytes<?>) chars);
        long n = 0;
        int shift = Long.SIZE - BITS_PER_CHAR;
        for (int i = 0; i < chars.length() && shift >= -1; i++) {
            int ch = chars.charAt(i);
            if (ch == '-') continue;
            long value = ch < PARSING.length ? PARSING[ch] : -1;
            if (value < 0) {
                return ~n;
            }
            n |= shift < 0 ? value >> -shift : value << shift;
            shift -= BITS_PER_CHAR;
        }
        return ~n;
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
                case 'z':
                    ch = '7';
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
