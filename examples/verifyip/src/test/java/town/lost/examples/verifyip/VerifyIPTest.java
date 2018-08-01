package town.lost.examples.verifyip;

import im.xcl.platform.api.StartBatch;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.TextMethodTester;
import net.openhft.chronicle.wire.TextWire;
import org.junit.Test;
import town.lost.examples.verifyip.api.Verify;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VerifyIPTest {
    public static void test(String basename) {
        TextMethodTester<VerifyIPTester> tester = new TextMethodTester<>(
                basename + "/in.yaml",
                VerifyIPTest::createGateway,
                VerifyIPTester.class,
                basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

    private static VanillaVerifyIP createGateway(VerifyIPTester tester) {
        return new VanillaVerifyIP(tester);
    }

    public static void main(String[] args) {
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

        Bytes publicKey2 = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey2 = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey2, secretKey2);

        TextWire wire = new TextWire(Bytes.elasticHeapByteBuffer(128));
        VerifyIPTester tester = wire.methodWriter(VerifyIPTester.class);
        tester.startBatch(new StartBatch(publicKey, 0));
        tester.verify(new Verify());
        tester.onConnection();
        tester.endBatch();

        System.out.println(wire);
    }

    @Test
    public void testVerifyOne() {
        test("verify/one");
    }
}
