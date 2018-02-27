package cash.xcl.api;

import cash.xcl.api.dto.ApplicationMessageEvent;
import cash.xcl.api.dto.ClusterTransferStep2Command;
import cash.xcl.api.dto.ClusterTransferStep3Command;
import cash.xcl.api.dto.ClusterTransferStep3Event;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.exch.ExecutionReportEvent;
import net.openhft.chronicle.core.io.Closeable;

/**
 * This should be only Events, and Commands which would be directed to other clusters.
 */
public interface ServerOut extends Closeable {

    void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent);

    void applicationMessageEvent(ApplicationMessageEvent applicationMessageEvent);

    void commandFailedEvent(CommandFailedEvent commandFailedEvent);

    void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command);

    void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command);

    void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event);

    void executionReportEvent(ExecutionReportEvent executionReportEvent);

}
