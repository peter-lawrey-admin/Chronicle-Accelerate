package cash.xcl.api;

import cash.xcl.api.dto.ClusterTransferStep2Command;
import cash.xcl.api.dto.ClusterTransferStep3Command;
import cash.xcl.api.dto.ClusterTransferStep3Event;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.exch.ExecutionReportEvent;
import cash.xcl.api.exch.OrderClosedEvent;
import net.openhft.chronicle.core.io.Closeable;

/**
 * This should be only Events, and Commands which would be directed to other clusters.
 */
public interface ServerOut extends ErrorMessageLogger, Closeable {

    void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent);

    void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command);

    void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command);

    void clusterTransferStep3Event(ClusterTransferStep3Event clusterTransferStep3Event);

    void executionReportEvent(ExecutionReportEvent executionReportEvent);

    void orderClosedEvent(OrderClosedEvent orderClosedEvent);

}
