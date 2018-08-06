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
                "0000 e9 00 00 00                                     # length\n" +
                        "0004 3f 3f 3f 3f                                     # format ????\n" +
                        "0008 97 ae 3e 01 32 31 49 1a ca 1b b8 9d 9d 31 6e 32 # signature start\n" +
                        "0018 36 45 ec a1 e0 e5 16 d8 96 d1 f7 3b 23 ad 32 85\n" +
                        "0028 88 e4 77 5b 09 35 ea 39 74 f9 f2 f4 32 d7 bd bd\n" +
                        "0038 f1 a7 ab ea f3 19 a7 83 b2 ea 09 04 56 77 fc 08 # signature end\n" +
                        "0048    01 00                                           # protocol\n" +
                        "004a    0a 00                                           # messageType\n" +
                        "004c    3a c0 48 a1 8b 59 da 29                         # address\n" +
                        "0054    0b 0a 09 08 07 06 05 00                         # timestampUS\n" +
                        "005c    7d 00 00 00 3f 3f 3f 3f 93 48 dc b7 65 4a b4 0c # origMessage\n" +
                        "006c    7f 8e b8 d7 32 a1 87 29 e1 d3 ce f8 37 d3 f4 c4\n" +
                        "007c    ea 61 95 45 47 7f a4 a7 3f 01 a3 33 72 08 3c 40\n" +
                        "008c    fa 91 b7 aa 79 67 5a 53 e6 65 72 7d a2 f6 e7 1f\n" +
                        "009c    0f 6f 26 e7 85 0d 6c 0c 01 00 02 00 3a c0 48 a1\n" +
                        "00ac    8b 59 da 29 0b 0a 09 08 07 06 05 00 20 3b 6a 27\n" +
                        "00bc    bc ce b6 a4 2d 62 a3 a8 d0 2a 6f 0d 73 65 32 15\n" +
                        "00cc    77 1d e2 43 a6 3a c0 48 a1 8b 59 da 29 0f 4e 6f # reason\n" +
                        "00dc    74 20 69 6d 70 6c 65 6d 65 6e 74 65 64\n",
                ae.toHexString());

        ae.verify(i -> publicKey);
    }
}
