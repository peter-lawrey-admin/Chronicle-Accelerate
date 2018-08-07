package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationErrorTest {
    @Test
    public void publicKey() {
        Bytes<Void> privateKey = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
        privateKey.zeroOut(0, privateKey.writeLimit());
        privateKey.writeSkip(privateKey.writeLimit());
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);

        CreateAccount ca = new CreateAccount(1, 2);
        SetTimeProvider timeProvider = new SetTimeProvider(0x05060708090a0bL * 1000);
        ca.sign(secretKey, timeProvider);

        ApplicationError ae = new ApplicationError(1, 10);
        ae.init(ca, "Not implemented");
        ae.sign(secretKey, timeProvider);

        assertEquals(
                "0000 e1 00 00 00                                     # length\n" +
                        "0004 31 54 3d 74 e0 cf 5e 6e 02 9b 72 22 98 89 ec 10 # signature start\n" +
                        "0014 e2 7e 04 d8 80 1d 06 30 bf a6 62 1a a5 df dc e1\n" +
                        "0024 85 61 07 ed 6c 30 ab 8e 0f 31 6b 8a 08 77 2a 90\n" +
                        "0034 14 27 8a 07 d1 f4 ed bb 69 4f 6a 36 0c b5 d8 01 # signature end\n" +
                        "0044    0a 00                                           # messageType\n" +
                        "0046    01 00                                           # protocol\n" +
                        "0048    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                        "0050    3a c0 48 a1 8b 59 da 29                         # address\n" +
                        "0058    79 00 00 00 96 de da 9f 15 2c 01 e1 93 0e 3f 49 # origMessage\n" +
                        "0068    14 4f d5 88 90 03 38 f7 6a 37 e8 32 8d 59 88 39\n" +
                        "0078    7c 9c 30 0c 1c 6f 8f fd b5 66 fd d1 a6 56 41 ee\n" +
                        "0088    37 dc ef df 33 a1 95 3c 0e 6b 1d 7b 2f bd bd 44\n" +
                        "0098    fc 42 97 0b 02 00 01 00 0b 0a 09 08 07 06 05 00\n" +
                        "00a8    3a c0 48 a1 8b 59 da 29 20 3b 6a 27 bc ce b6 a4\n" +
                        "00b8    2d 62 a3 a8 d0 2a 6f 0d 73 65 32 15 77 1d e2 43\n" +
                        "00c8    a6 3a c0 48 a1 8b 59 da 29 0f 4e 6f 74 20 69 6d # reason\n" +
                        "00d8    70 6c 65 6d 65 6e 74 65 64\n",
                ae.toHexString());

        assertEquals("!im.xcl.platform.api.ApplicationError {\n" +
                "  messageType: 10,\n" +
                "  protocol: 1,\n" +
                "  timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "  address: \"29da598ba148c03a\",\n" +
                "  origMessage: !im.xcl.platform.api.CreateAccount {\n" +
                "    messageType: 2,\n" +
                "    protocol: 1,\n" +
                "    timestampUS: \"2014-10-22T18:22:32.901131\",\n" +
                "    address: \"29da598ba148c03a\",\n" +
                "    publicKey: !!binary O2onvM62pC1io6jQKm8Nc2UyFXcd4kOmOsBIoYtZ2ik=\n" +
                "  },\n" +
                "  reason: Not implemented\n" +
                "}\n", ae.toString());
        ae.verify(i -> publicKey);
    }
}
