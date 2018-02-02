package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class NewAddressRejectedEvent extends SignedMessage {
    CreateNewAddressCommand command;
    int reasonCode;
    String reason;

    public NewAddressRejectedEvent(long sourceAddress, long eventTime, CreateNewAddressCommand command, int reasonCode, String reason) {
        super(sourceAddress, eventTime);
        this.command = command;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public NewAddressRejectedEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        BytesIn<?> in = bytes;
        command = in.readMarshallableLength16(CreateNewAddressCommand.class, command);
        command.readMarshallable(bytes);
        reasonCode = bytes.readInt();
        reason = bytes.readUtf8();
    }

    @Override
    protected void writeMarshallable2(Bytes bytes) {
        command.writeMarshallable(bytes);
        bytes.writeInt(reasonCode);
        bytes.writeUtf8(reason);
    }

    @Override
    public int messageType() {
        return MethodIds.NEW_ADDRESS_REJECTED_EVENT;
    }

    public CreateNewAddressCommand command() {
        return command;
    }

    public NewAddressRejectedEvent command(CreateNewAddressCommand command) {
        this.command = command;
        return this;
    }

    public int reasonCode() {
        return reasonCode;
    }

    public NewAddressRejectedEvent reasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String reason() {
        return reason;
    }

    public NewAddressRejectedEvent reason(String reason) {
        this.reason = reason;
        return this;
    }
}
