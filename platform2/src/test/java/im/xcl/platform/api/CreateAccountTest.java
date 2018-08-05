package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CreateAccountTest {

    @Test
    public void publicKey() {
        Bytes<Void> privateKey = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
        privateKey.zeroOut(0, privateKey.writeLimit());
        privateKey.writeSkip(privateKey.writeLimit());
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);
        assertEquals("00000000 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00 ········ ········\n" +
                "........\n" +
                "00000020 3b 6a 27 bc ce b6 a4 2d  62 a3 a8 d0 2a 6f 0d 73 ;j'····- b···*o·s\n" +
                "00000030 65 32 15 77 1d e2 43 a6  3a c0 48 a1 8b 59 da 29 e2·w··C· :·H··Y·)\n", secretKey.toHexString());
        CreateAccount ca = new CreateAccount(1, 2);
        ca.sign(secretKey, new SetTimeProvider(0x05060708090a0bL * 1000));
        assertEquals("00000000 3b 6a 27 bc ce b6 a4 2d  62 a3 a8 d0 2a 6f 0d 73 ;j'····- b···*o·s\n" +
                        "00000010 65 32 15 77 1d e2 43 a6  3a c0 48 a1 8b 59 da 29 e2·w··C· :·H··Y·)\n",
                ca.publicKey().bytesForRead().toHexString());
        assertEquals("7d 00 00 00                                     # length\n" +
                "3f 3f 3f 3f                                     # format ????\n" +
                "93 48 dc b7 65 4a b4 0c 7f 8e b8 d7 32 a1 87 29 # signature start\n" +
                "e1 d3 ce f8 37 d3 f4 c4 ea 61 95 45 47 7f a4 a7\n" +
                "3f 01 a3 33 72 08 3c 40 fa 91 b7 aa 79 67 5a 53\n" +
                "e6 65 72 7d a2 f6 e7 1f 0f 6f 26 e7 85 0d 6c 0c # signature end\n" +
                "   01 00                                           # protocol\n" +
                "   02 00                                           # messageType\n" +
                "   3a c0 48 a1 8b 59 da 29                         # address\n" +
                "   0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "   20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d # publicKey\n" +
                "   73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59 da\n" +
                "   29\n", ca.toHexString());

        assertTrue(ca.verify(this::selfSigning));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}