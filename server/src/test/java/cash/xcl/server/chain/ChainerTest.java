package cash.xcl.server.chain;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.TransferValueCommand;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Mocker;
import org.junit.Test;

import java.io.StringWriter;

public class ChainerTest {
    @Test
    public void testTransactions() {
        // todo turn this into a real test.
        Chainer chainer = new Chainer(10, new long[1], tbe -> {
        });
        StringWriter out = new StringWriter();
        chainer.allMessagesLookup(addressOrRegion -> Mocker.logging(AllMessages.class, "to " + addressOrRegion + " ", out));
        TransferValueCommand xcl = new TransferValueCommand(0, 1, 0, 1, "XCL", "");
        for (int i = 0; i < 1000; i++) {
            Jvm.pause(1);
            xcl.eventTime(1000 + i);
            chainer.transferValueCommand(xcl);
        }
        Jvm.pause(25);
        chainer.close();
        System.out.println(out);
    }
}
