package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;

public class ClusterTransferValueRejectedEvent extends SignedMessage {
    ClusterTransferValueCommand command;
    int reasonCode;
    String reason;

    public ClusterTransferValueRejectedEvent(long sourceAddress, long eventTime, ClusterTransferValueCommand command, int reasonCode, String reason) {
        super(sourceAddress, eventTime);
        this.command = command;
        this.reasonCode = reasonCode;
        this.reason = reason;
    }

    public ClusterTransferValueRejectedEvent() {

    }

    @Override
    protected void readMarshallable2(BytesIn bytes) {
        BytesIn<?> in = bytes;
        command = in.readMarshallableLength16(ClusterTransferValueCommand.class, command);
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
    protected int messageType() {
        return MethodIds.CLUSTER_TRANSFER_VALUE_REJECTED_EVENT;
    }

    public ClusterTransferValueCommand command() {
        return command;
    }

    public ClusterTransferValueRejectedEvent command(ClusterTransferValueCommand command) {
        this.command = command;
        return this;
    }

    public int reasonCode() {
        return reasonCode;
    }

    public ClusterTransferValueRejectedEvent reasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
        return this;
    }

    public String reason() {
        return reason;
    }

    public ClusterTransferValueRejectedEvent reason(String reason) {
        this.reason = reason;
        return this;
    }
}
