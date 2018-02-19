package cash.xcl.server;

import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.time.SetTimeProvider;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.time.TimeProvider;

public class MainPostBlockChainProcessor extends LocalPostBlockChainProcessor {
    private final AddressService addressService;

    public MainPostBlockChainProcessor(long address, AddressService addressService) {
        super(address);
        this.addressService = addressService;
    }

    TimeProvider timeProvider = SystemTimeProvider.INSTANCE;

    // Used for testing purposes
    MainPostBlockChainProcessor(AllMessagesServer allMessagesServer) {
        super(121212121212L);
        addressService = new AddressService();
        allMessagesLookup(allMessagesServer);
    }

    public void time(long time) {
        if (timeProvider instanceof SystemTimeProvider)
            timeProvider = new SetTimeProvider();
        ((SetTimeProvider) timeProvider).currentTimeMicros(time);
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
            to(sourceAddress)
                    .createNewAddressEvent(
                            createNewAddressEvent);
        } catch (Exception e) {
            to(sourceAddress)
                    .commandFailedEvent(
                            new CommandFailedEvent(address, eventTime, createNewAddressCommand, e.toString()));
        }
    }
}
