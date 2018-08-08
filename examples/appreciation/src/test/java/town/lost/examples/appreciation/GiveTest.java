package town.lost.examples.appreciation;

import im.xcl.platform.util.XCLUtil;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.TimeProvider;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.TextMethodTester;
import net.openhft.chronicle.wire.TextWire;
import org.junit.Test;
import town.lost.examples.appreciation.api.Give;
import town.lost.examples.appreciation.api.OpeningBalance;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class GiveTest {
    public static void test(String basename) {
        TextMethodTester<AppreciationTester> tester = new TextMethodTester<>(
                basename + "/in.yaml",
                GiveTest::createGateway,
                AppreciationTester.class,
                basename + "/out.yaml");
        tester.setup(basename + "/setup.yaml");
        try {
            tester.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        assertEquals(tester.expected(), tester.actual());
    }

    private static VanillaAppreciationGateway createGateway(AppreciationTester tester) {
        VanillaBalanceStore balanceStore = new VanillaBalanceStore();
        VanillaAppreciationTransactionListener blockchain = new VanillaAppreciationTransactionListener(tester, balanceStore);
        return new VanillaAppreciationGateway(tester,
                blockchain,
                balanceStore);
    }

    public static void main(String[] args) {
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

        Bytes publicKey2 = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey2 = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey2, secretKey2);

        TextWire wire = new TextWire(Bytes.elasticHeapByteBuffer(128));
        AppreciationTester tester = wire.methodWriter(AppreciationTester.class);
        tester.openingBalance(new OpeningBalance(publicKey, 100)
                .address(XCLUtil.toAddress(publicKey))
                .account(publicKey)
                .timestampUS(TimeProvider.get().currentTimeMicros()));
        tester.openingBalance(new OpeningBalance(publicKey2, 30)
                .address(XCLUtil.toAddress(publicKey))
                .account(publicKey2)
                .timestampUS(TimeProvider.get().currentTimeMicros()));
        tester.give(new Give()
                .address(XCLUtil.toAddress(publicKey))
                .timestampUS(TimeProvider.get().currentTimeMicros())
                .init(XCLUtil.toAddress(publicKey2), 20.0));

        System.out.println(wire);
    }

    @Test
    public void testGiveOne() {
        test("give/one");
    }
}
