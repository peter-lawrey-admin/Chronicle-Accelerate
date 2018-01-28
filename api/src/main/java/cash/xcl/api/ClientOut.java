package cash.xcl.api;

import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.TransferValueCommand;

public interface ClientOut {
    void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand);

    void transferValueCommand(TransferValueCommand transferValueCommand);
}
