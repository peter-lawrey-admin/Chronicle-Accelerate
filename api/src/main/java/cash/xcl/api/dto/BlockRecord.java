package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

public class BlockRecord extends AbstractBytesMarshallable {
    long address;
    long blockNumber;
    int status;

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {
        address = bytes.readLong();
        blockNumber = bytes.readUnsignedInt();
        status = bytes.readInt();
    }

    @Override
    public void writeMarshallable(BytesOut bytes) {
        bytes.writeLong(address);
        bytes.writeUnsignedInt(blockNumber);
        bytes.writeInt(status);
    }

    public long address() {
        return address;
    }

    public BlockRecord address(long address) {
        this.address = address;
        return this;
    }

    public long blockNumber() {
        return blockNumber;
    }

    public BlockRecord blockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
        return this;
    }

    public int status() {
        return status;
    }

    public BlockRecord status(int status) {
        this.status = status;
        return this;
    }
}
