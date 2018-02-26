package cash.xcl.server.mock;

import cash.xcl.api.ServerIn;
import cash.xcl.api.ServerOut;
import cash.xcl.api.dto.CommandFailedEvent;
import cash.xcl.api.dto.CreateNewAddressCommand;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.api.util.XCLBase32;
import cash.xcl.server.AddressService;
import net.openhft.chronicle.bytes.Bytes;

public class MockServer extends AbstractAllMessages implements ServerIn {
    private final AddressService addressService = new AddressService();

    private ServerOut serverOut;

    public MockServer(ServerOut serverOut) {
        super(0);
        this.serverOut = serverOut;
    }

    public long sourceAddress() {
        return address;
    }

    public MockServer sourceAddress(long sourceAddress) {
        this.address = sourceAddress;
        return this;
    }

    public long eventTime() {
        return 0; // for reproducibility;
    }

    public MockServer serverOut(ServerOut serverOut) {
        this.serverOut = serverOut;
        return this;
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        String region = XCLBase32.normalize(createNewAddressCommand.region());
        try {
            Bytes publicKey = createNewAddressCommand.publicKey();
            long address = addressService.createAddress(region, publicKey);
            serverOut.createNewAddressEvent(
                    new CreateNewAddressEvent(sourceAddress(), eventTime(), createNewAddressCommand, address, publicKey));

        } catch (IllegalArgumentException iae) {
            serverOut.commandFailedEvent(
                    new CommandFailedEvent(sourceAddress(), eventTime(), createNewAddressCommand, iae.getMessage()));
        }
    }
}
