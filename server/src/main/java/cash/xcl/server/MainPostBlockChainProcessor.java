package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;

public class MainPostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final AddressService addressService;

    public MainPostBlockChainProcessor(long address, AddressService addressService) {
        super(address);
        this.addressService = addressService;
    }

    // Used for testing purposes
    MainPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(121212121212L);
        addressService = new AddressService();
        allMessagesLookup(allMessagesServer);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        long eventTime = timeProvider.currentTimeMicros();
        long sourceAddress = createNewAddressCommand.sourceAddress();
        try {
            long newAddress = createNewAddressCommand.newAddressSeed();
            Bytes publicKey = createNewAddressCommand.publicKey();
            addressService.addAddress(newAddress, publicKey);
            CreateNewAddressEvent createNewAddressEvent = new CreateNewAddressEvent(address, eventTime, createNewAddressCommand, newAddress, publicKey);
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.createNewAddressEvent(createNewAddressEvent);
        } catch (Exception e) {
            Jvm.warn().on(getClass(), e.toString());
            CommandFailedEvent cfe = new CommandFailedEvent(address, eventTime, createNewAddressCommand, e.toString());
            AllMessages messageWriter = to(sourceAddress);
            messageWriter.commandFailedEvent(cfe);
        }
    }
}
