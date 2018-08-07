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
        assertEquals("0000 79 00 00 00                                     # length\n" +
                "0004 96 de da 9f 15 2c 01 e1 93 0e 3f 49 14 4f d5 88 # signature start\n" +
                "0014 90 03 38 f7 6a 37 e8 32 8d 59 88 39 7c 9c 30 0c\n" +
                "0024 1c 6f 8f fd b5 66 fd d1 a6 56 41 ee 37 dc ef df\n" +
                "0034 33 a1 95 3c 0e 6b 1d 7b 2f bd bd 44 fc 42 97 0b # signature end\n" +
                "0044    02 00                                           # messageType\n" +
                "0046    01 00                                           # protocol\n" +
                "0048    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "0050    3a c0 48 a1 8b 59 da 29                         # address\n" +
                "0058    20 3b 6a 27 bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d # publicKey\n" +
                "0068    73 65 32 15 77 1d e2 43 a6 3a c0 48 a1 8b 59 da\n" +
                "0078    29\n", ca.toHexString());

        assertEquals("!im.xcl.platform.api.CreateAccount {\n" +
                "  messageType: 2,\n" +
                "  protocol: 1,\n" +
                "  timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "  address: \"29da598ba148c03a\",\n" +
                "  publicKey: !!binary O2onvM62pC1io6jQKm8Nc2UyFXcd4kOmOsBIoYtZ2ik=\n" +
                "}\n", ca.toString());

        assertTrue(ca.verify(this::selfSigning));

        OnAccountCreated created = new OnAccountCreated(1, 3)
                .createAccount(ca);

        created.sign(secretKey, timeProvider);
        assertEquals("0000 d1 00 00 00                                     # length\n" +
                "0004 94 6d 54 66 b0 0d 6c a7 54 b9 52 49 a3 0f 3d a9 # signature start\n" +
                "0014 fb f7 ad c7 ae 97 b4 0f cb f1 05 ee 5e 66 28 c3\n" +
                "0024 49 d8 d0 ce 7d db d4 59 01 63 5e 75 a1 f4 9a 45\n" +
                "0034 22 d2 a6 f7 85 c3 0f f8 cc a9 8e d7 2b e3 59 07 # signature end\n" +
                "0044    03 00                                           # messageType\n" +
                "0046    01 00                                           # protocol\n" +
                "0048    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                "0050    3a c0 48 a1 8b 59 da 29                         # address\n" +
                "0058    79 00 00 00 96 de da 9f 15 2c 01 e1 93 0e 3f 49 # createAccount\n" +
                "0068    14 4f d5 88 90 03 38 f7 6a 37 e8 32 8d 59 88 39\n" +
                "0078    7c 9c 30 0c 1c 6f 8f fd b5 66 fd d1 a6 56 41 ee\n" +
                "0088    37 dc ef df 33 a1 95 3c 0e 6b 1d 7b 2f bd bd 44\n" +
                "0098    fc 42 97 0b 02 00 01 00 0b 0a 09 08 07 06 05 00\n" +
                "00a8    3a c0 48 a1 8b 59 da 29 20 3b 6a 27 bc ce b6 a4\n" +
                "00b8    2d 62 a3 a8 d0 2a 6f 0d 73 65 32 15 77 1d e2 43\n" +
                "00c8    a6 3a c0 48 a1 8b 59 da 29\n", created.toHexString());

        assertEquals("!im.xcl.platform.api.OnAccountCreated {\n" +
                "  messageType: 3,\n" +
                "  protocol: 1,\n" +
                "  timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "  address: \"29da598ba148c03a\",\n" +
                "  createAccount: {\n" +
                "    messageType: 2,\n" +
                "    protocol: 1,\n" +
                "    timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "    address: \"29da598ba148c03a\",\n" +
                "    publicKey: !!binary O2onvM62pC1io6jQKm8Nc2UyFXcd4kOmOsBIoYtZ2ik=\n" +
                "  }\n" +
                "}\n", created.toString());

        assertTrue(created.verify(i -> publicKey));

    }

    private <T> T selfSigning(long i) {
        fail("Self signing");
        return null;
    }
}