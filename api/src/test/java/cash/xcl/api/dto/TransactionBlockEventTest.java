package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.Marshallable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TransactionBlockEventTest {

    @Ignore
    @Test
    public void modifyMessageAfterSigning() {
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
        CreateNewAddressCommand command = new CreateNewAddressCommand(1, 2, publicKey, "usny");
        command.newAddressSeed(111111111111111L);
        System.out.println("BEFORE SIGNING: " + command.toString());

        Bytes bytes = Bytes.allocateElasticDirect();
        if (!command.hasSignature()) {
            command.eventTime(1);
            bytes.clear();
            command.sign(bytes, 1, secretKey);
        }
        System.out.println("AFTER SIGNING: " + command.toString());

        command.newAddressSeed(2222222222222222L);
        System.out.println("AFTER SETTING TO 2222222222222222L: " + command.toString());

        TransactionBlockEvent tbe = new TransactionBlockEvent().region(-1);
        tbe.addTransaction(command);
        System.out.println("AFTER ADDING TO THE TBE: " + command.toString());

        System.out.println("TBE: " + tbe.toString());

        Assert.assertThat(tbe.toString(), CoreMatchers.containsString("newAddressSeed: 2222222222222222"));
    }


    @Test
    public void writeMarshallable() {
        TransactionBlockEvent tbe = new TransactionBlockEvent()
                .region("GBLDN")
                .addTransaction(
                        new CreateNewAddressCommand(1, 2, Bytes.wrapForRead(new byte[32]), "gb1nd"))
                .addTransaction(
                        new TransferValueCommand(0, 3, 1, 1.23, "XCL", "init"));

        assertEquals("!TransactionBlockEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  region: GBLDN,\n" +
                "  weekNumber: 0,\n" +
                "  blockNumber: 0,\n" +
                "  transactions: [\n" +
                "    !CreateNewAddressCommand { sourceAddress: 1, eventTime: 2, publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=, region: GBLND, newAddressSeed: 0 },\n" +
                "    !TransferValueCommand { sourceAddress: 0, eventTime: 3, toAddress: 1, amount: 1.23, currency: XCL, reference: init }\n" +
                "  ]\n" +
                "}\n", tbe.toString().replaceAll("\r", ""));
    }

    @Test
    public void readMarshallable() {
        TransactionBlockEvent tbe = Marshallable.fromString("!cash.xcl.api.dto.TransactionBlockEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  region: gb1dn,\n" +
                "  weekNumber: 0,\n" +
                "  blockNumber: 0,\n" +
                "  transactions: [\n" +
                "    !cash.xcl.api.dto.CreateNewAddressCommand { sourceAddress: 1, eventTime: 2, publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=, region: gb1dn },\n" +
                "    !cash.xcl.api.dto.TransferValueCommand { sourceAddress: 0, eventTime: 3, toAddress: 1, amount: 1.23, currency: XCL, reference: init }\n" +
                "  ]\n" +
                "}\n");
        StringWriter out = new StringWriter();
        tbe.replay(Mocker.logging(AllMessages.class, "out ", out));
        assertEquals("out createNewAddressCommand[!CreateNewAddressCommand {\n" +
                "  sourceAddress: 1,\n" +
                "  eventTime: 2,\n" +
                "  publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=,\n" +
                "  region: GBLDN,\n" +
                "  newAddressSeed: 0\n" +
                "}\n" +
                "]\n" +
                "out transferValueCommand[!TransferValueCommand {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 3,\n" +
                "  toAddress: 1,\n" +
                "  amount: 1.23,\n" +
                "  currency: XCL,\n" +
                "  reference: init\n" +
                "}\n" +
                "]\n", out.toString().replaceAll("\r", ""));

    }

    @Test
    public void testAdding10Kmessages() {
        TransactionBlockEvent tbe = new TransactionBlockEvent(TransactionBlockEvent._32_MB, false);
        tbe.region("gb1nd");
        TransferValueCommand tvc = new TransferValueCommand(0, 3, 1, 1.23, "XCL", "init");
        for (int i = 0; i < 1_000_000; i++) {
            tbe.addTransaction(tvc);
        }
    }
}