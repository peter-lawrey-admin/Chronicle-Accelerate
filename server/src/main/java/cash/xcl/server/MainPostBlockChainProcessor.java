package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import net.openhft.chronicle.bytes.Bytes;

public class MainPostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final AddressService addressService;

    public MainPostBlockChainProcessor(long address, AddressService addressService) {
        super(address);
        this.addressService = addressService;
    }

    // Used for testing purposes
    MainPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(0);
        addressService = new AddressService();
        allMessagesLookup(allMessagesServer);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        long newAddress = createNewAddressCommand.newAddressSeed();
        Bytes publicKey = createNewAddressCommand.publicKey();
        addressService.addAddress(newAddress, publicKey);
        long sourceAddress = createNewAddressCommand.sourceAddress();
        to(sourceAddress)
                .createNewAddressEvent(new CreateNewAddressEvent(address, 0, createNewAddressCommand, newAddress, publicKey));
    }
}
