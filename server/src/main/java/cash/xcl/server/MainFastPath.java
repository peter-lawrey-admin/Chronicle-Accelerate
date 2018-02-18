package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CreateNewAddressCommand;
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

}
