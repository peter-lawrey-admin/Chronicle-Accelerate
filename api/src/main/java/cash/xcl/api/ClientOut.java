package cash.xcl.api;

import cash.xcl.api.dto.ClusterTransferValueCommand;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.TransferValueCommand;
import net.openhft.chronicle.core.io.Closeable;

public interface ClientOut extends Closeable {
    void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand);

    void transferValueCommand(TransferValueCommand transferValueCommand);

    //void clusterTransferValueCommand(ClusterTransferValueCommand clusterTransferValueCommand);
}
