package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VerificationTest {

    @Test
    public void publicKey() {
        Bytes<Void> privateKey = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
        privateKey.zeroOut(0, privateKey.writeLimit());
        privateKey.writeSkip(privateKey.writeLimit());
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);

        Bytes<Void> privateKey2 = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
        privateKey2.zeroOut(0, privateKey2.writeLimit());
        privateKey2.writeSkip(privateKey2.writeLimit());
        privateKey2.writeUnsignedByte(0, 1);
        Bytes publicKey2 = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey2 = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.privateToPublicAndSecret(publicKey2, secretKey2, privateKey2);

        assertEquals("00000000 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 ········ ········\n" +
                "........\n" +
                "00000020 3b 6a 27 bc ce b6 a4 2d  62 a3 a8 d0 2a 6f 0d 73 ;j'····- b···*o·s\n" +
                "00000030 65 32 15 77 1d e2 43 a6  3a c0 48 a1 8b 59 da 29 e2·w··C· :·H··Y·)\n", secretKey.toHexString());
        Verification v = new Verification(1, 22);
        v.keyVerified(publicKey);
        SetTimeProvider timeProvider = new SetTimeProvider(0x05060708090a0bL * 1000);
        v.sign(secretKey2, timeProvider);

        assertEquals("0000 9a 00 00 00                                     # length\n" +
                "0004 62 1b 02 4b a1 9a 76 c6 84 28 68 9d 35 56 dc fb # signature start\n" +
                "0014 4c 4a 49 dd 7b 92 ab 84 db 6a b8 3a ee 94 6d 40\n" +
                "0024 6e e8 44 1f 09 5c 80 c5 4d 25 2a 9a 63 7c 45 87\n" +
                "0034 2b 9a 56 44 78 1b de 58 f5 84 4d 03 ff 8f 0f 09 # signature end\n" +
                "0044    16 00                                           # messageType\n" +
                "0046    01 00                                           # protocol\n" +
                "0048    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "0050    e6 df 06 5d 68 3b d4 fc                         # address\n" +
                "0058    20 ce cc 15 07 dc 1d dd 72 95 95 1c 29 08 88 f0 # publicKey\n" +
                "0068    95 ad b9 04 4d 1b 73 d6 96 e6 df 06 5d 68 3b d4\n" +
                "0078    fc 20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f # keyVerified\n" +
                "0088    0d 73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59\n" +
                "0098    da 29\n", v.toHexString());

        assertEquals("!im.xcl.platform.api.Verification {\n" +
                "  messageType: 22,\n" +
                "  protocol: 1,\n" +
                "  timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "  address: fcd43b685d06dfe6,\n" +
                "  publicKey: !!binary zswVB9wd3XKVlRwpCIjwla25BE0bc9aW5t8GXWg71Pw=,\n" +
                "  keyVerified: !!binary O2onvM62pC1io6jQKm8Nc2UyFXcd4kOmOsBIoYtZ2ik=\n" +
                "}\n", v.toString());

        assertTrue(v.verify(i -> privateKey2));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}