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

        assertEquals("0000 7d 00 00 00                                     # length\n" +
                "0004 3f 3f 3f 3f                                     # format ????\n" +
                "0008 1b 2b a9 6c de 0e 42 4c 2e 34 99 08 c5 05 84 69 # signature start\n" +
                "0018 cf db ef f5 66 ae 59 0a 39 4e f6 a4 0e 86 29 3b\n" +
                "0028 b9 60 eb 24 a4 21 d9 64 9d 78 4b c8 59 29 a7 d6\n" +
                "0038 d0 4d d0 e3 ae c0 37 0d 20 53 bd 33 38 3d 90 04 # signature end\n" +
                "0048    01 00                                           # protocol\n" +
                "004a    16 00                                           # messageType\n" +
                "004c    e6 df 06 5d 68 3b d4 fc                         # address\n" +
                "0054    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "005c    20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d # keyVerified\n" +
                "006c    73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59 da\n" +
                "007c    29\n", v.toHexString());

        assertTrue(v.verify(i -> privateKey2));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}