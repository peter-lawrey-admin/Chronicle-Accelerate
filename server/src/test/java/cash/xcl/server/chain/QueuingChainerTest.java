package cash.xcl.server.chain;

import org.junit.Test;

public class QueuingChainerTest {
    @Test
    public void testTransactions() {
      /*  // todo turn this into a real test.
        QueuingChainer chainer = new QueuingChainer("gb1dn", 10, new long[1], tbe -> {
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
        System.out.println(out);*/
    }
}
