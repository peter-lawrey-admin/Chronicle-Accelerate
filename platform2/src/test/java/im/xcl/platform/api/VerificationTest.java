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

        assertEquals("0000 9e 00 00 00                                     # length\n" +
                "0004 3f 3f 3f 3f                                     # format ????\n" +
                "0008 20 8b a0 16 1a 72 51 5c 80 8c 7f 5c 19 bd 19 be # signature start\n" +
                "0018 45 d8 09 4e 7e 8e 1b 72 e3 c9 f6 cf e9 a2 3b 3d\n" +
                "0028 ab 13 b4 38 68 bd 07 f5 5d 17 47 b4 3c 77 72 79\n" +
                "0038 b5 19 86 25 99 96 a2 54 77 ca 9f 65 1a 75 8e 00 # signature end\n" +
                "0048    01 00                                           # protocol\n" +
                "004a    16 00                                           # messageType\n" +
                "004c    e6 df 06 5d 68 3b d4 fc                         # address\n" +
                "0054    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "005c    20 ce cc 15 07 dc 1d dd 72 95 95 1c 29 08 88 f0 # publicKey\n" +
                "006c    95 ad b9 04 4d 1b 73 d6 96 e6 df 06 5d 68 3b d4\n" +
                "007c    fc " +
                "20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f # keyVerified\n" +
                "008c    0d 73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59\n" +
                "009c    da 29\n", v.toHexString());

        assertTrue(v.verify(i -> privateKey2));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}