package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.ClusterTransferStep3Event;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.OpeningBalanceEvent;
import cash.xcl.api.dto.TransferValueCommand;
import cash.xcl.api.util.AbstractAllMessages;

public class MainFastPath extends AbstractAllMessages {
    private final AllMessages chainer;
    private final AddressService addressService;

    public MainFastPath(long address, AllMessages chainer, AddressService addressService) {
        super(address);
        this.chainer = chainer;
        this.addressService = addressService;
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        createNewAddressCommand.newAddressSeed(addressService.generateAddress(createNewAddressCommand.region()));
        // TODO validate
        chainer.createNewAddressCommand(createNewAddressCommand);
    }


    // TODO do we need a LocalFastPath ?

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        // TODO validate
        chainer.openingBalanceEvent(openingBalanceEvent);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        // TODO validate
        chainer.transferValueCommand(transferValueCommand);
    }


}
