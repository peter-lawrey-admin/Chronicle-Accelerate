package cash.xcl.api.dto;

import cash.xcl.api.AllMessages;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Mocker;
import net.openhft.chronicle.wire.Marshallable;
import org.junit.Test;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class TransactionBlockEventTest {

    @Test
    public void writeMarshallable() {
        TransactionBlockEvent tbe = new TransactionBlockEvent()
                .region("gb1nd")
                .addTransaction(
                        new CreateNewAddressCommand(1, 2, Bytes.wrapForRead(new byte[32]), "gb1nd"))
                .addTransaction(
                        new TransferValueCommand(0, 3, 1, 1.23, "XCL", "init"));

        assertEquals("!TransactionBlockEvent {\n" +
                "  sourceAddress: 0,\n" +
                "  eventTime: 0,\n" +
                "  region: gb1dn,\n" +
                "  weekNumber: 0,\n" +
                "  blockNumber: 0,\n" +
                "  transactions: [\n" +
                "    !CreateNewAddressCommand { sourceAddress: 1, eventTime: 2, publicKey: !!binary AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=, region: gb1dn },\n" +
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
                "  region: gb1dn\n" +
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
}