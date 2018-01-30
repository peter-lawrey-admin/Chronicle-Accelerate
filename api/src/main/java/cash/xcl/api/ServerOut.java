package cash.xcl.api;

import cash.xcl.api.dto.ApplicationMessageEvent;
import cash.xcl.api.dto.ClusterTransferValueCommand;
import cash.xcl.api.dto.NewAddressRejectedEvent;
import net.openhft.chronicle.core.io.Closeable;

public interface ServerOut extends Closeable {
    void newAddressRejectedEvent(NewAddressRejectedEvent newAddressRejectedEvent);

    void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent);

    void clusterTransferValueCommand(ClusterTransferValueCommand clusterTransferValueCommand);
}
