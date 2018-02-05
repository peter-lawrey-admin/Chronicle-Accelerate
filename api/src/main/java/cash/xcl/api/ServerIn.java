package cash.xcl.api;

import cash.xcl.api.dto.ClusterTransferStep2Command;
import cash.xcl.api.dto.ClusterTransferStep3Command;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.WithdrawValueCommand;
import net.openhft.chronicle.core.io.Closeable;

/**
 * This should be only Commands (no Query/Response) and weekly Events or Events from other clusters.
 */
public interface ServerIn extends Closeable {
    // inter cluster commands
    void clusterTransferStep2Command(ClusterTransferStep2Command clusterTransferStep2Command);

    void clusterTransferStep3Command(ClusterTransferStep3Command clusterTransferStep3Command);

    // request to deposit funds from an external gateway
    void depositValueCommand(DepositValueCommand depositValueCommand);

    void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand);

}
