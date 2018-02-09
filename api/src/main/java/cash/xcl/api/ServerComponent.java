package cash.xcl.api;

import cash.xcl.api.tcp.XCLServer;

public interface ServerComponent extends AllMessages {
    void xclServer(XCLServer xclServer);
}
