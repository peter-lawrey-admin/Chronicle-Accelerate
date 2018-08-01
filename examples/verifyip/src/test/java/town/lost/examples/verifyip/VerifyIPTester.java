package town.lost.examples.verifyip;

import im.xcl.platform.api.MessageRouter;
import town.lost.examples.verifyip.api.VerifyIP;
import town.lost.examples.verifyip.api.VerifyIPResponse;

public interface VerifyIPTester extends VerifyIP, MessageRouter<VerifyIPResponse>, VerifyIPResponse {
}
