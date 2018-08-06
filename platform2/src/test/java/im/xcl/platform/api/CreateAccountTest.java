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
        SetTimeProvider timeProvider = new SetTimeProvider(0x05060708090a0bL * 1000);
        ca.sign(secretKey, timeProvider);
        assertEquals("00000000 3b 6a 27 bc ce b6 a4 2d  62 a3 a8 d0 2a 6f 0d 73 ;j'····- b···*o·s\n" +
                        "00000010 65 32 15 77 1d e2 43 a6  3a c0 48 a1 8b 59 da 29 e2·w··C· :·H··Y·)\n",
                ca.publicKey().bytesForRead().toHexString());
        assertEquals("0000 7d 00 00 00                                     # length\n" +
                "0004 3f 3f 3f 3f                                     # format ????\n" +
                "0008 93 48 dc b7 65 4a b4 0c 7f 8e b8 d7 32 a1 87 29 # signature start\n" +
                "0018 e1 d3 ce f8 37 d3 f4 c4 ea 61 95 45 47 7f a4 a7\n" +
                "0028 3f 01 a3 33 72 08 3c 40 fa 91 b7 aa 79 67 5a 53\n" +
                "0038 e6 65 72 7d a2 f6 e7 1f 0f 6f 26 e7 85 0d 6c 0c # signature end\n" +
                "0048    01 00                                           # protocol\n" +
                "004a    02 00                                           # messageType\n" +
                "004c    3a c0 48 a1 8b 59 da 29                         # address\n" +
                "0054    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "005c    20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d # publicKey\n" +
                "006c    73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59 da\n" +
                "007c    29\n", ca.toHexString());

        assertTrue(ca.verify(this::selfSigning));

        OnAccountCreated created = new OnAccountCreated(1, 3)
                .createAccount(ca);

        created.sign(secretKey, timeProvider);
        assertEquals("0000 d9 00 00 00                                     # length\n" +
                "0004 3f 3f 3f 3f                                     # format ????\n" +
                "0008 fa 35 c1 c8 6f 2c 17 34 1f bf ec 36 e3 2b d5 e3 # signature start\n" +
                "0018 b8 2d 98 ca 41 24 fb 8b 04 df 42 64 3b 15 0d c9\n" +
                "0028 3c 11 aa b2 ac 02 0d 99 55 46 fc b7 6d a5 1f 4a\n" +
                "0038 0f 9c 8d 5c 70 b0 db 3d 90 fe 7a 80 2d 7a 5c 0f # signature end\n" +
                "0048    01 00                                           # protocol\n" +
                "004a    03 00                                           # messageType\n" +
                "004c    3a c0 48 a1 8b 59 da 29                         # address\n" +
                "0054    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "005c    7d 00 00 00 3f 3f 3f 3f 93 48 dc b7 65 4a b4 0c # createAccount\n" +
                "006c    7f 8e b8 d7 32 a1 87 29 e1 d3 ce f8 37 d3 f4 c4\n" +
                "007c    ea 61 95 45 47 7f a4 a7 3f 01 a3 33 72 08 3c 40\n" +
                "008c    fa 91 b7 aa 79 67 5a 53 e6 65 72 7d a2 f6 e7 1f\n" +
                "009c    0f 6f 26 e7 85 0d 6c 0c 01 00 02 00 3a c0 48 a1\n" +
                "00ac    8b 59 da 29 0b 0a 09 08 07 06 05 00 20 3b 6a 27\n" +
                "00bc    bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d 73 65 32 15\n" +
                "00cc    77 1d e2 43 a6 3a c0 48 a1 8b 59 da 29\n", created.toHexString());

        assertTrue(created.verify(i -> publicKey));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}