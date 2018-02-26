package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.salt.Ed25519;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;
import net.openhft.chronicle.wire.Marshallable;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public abstract class SignedMessage extends AbstractBytesMarshallable {
    static {
        ClassAliasPool.CLASS_ALIASES.addAlias(
                TransactionBlockEvent.class,
                TransactionBlockGossipEvent.class,
                TransactionBlockVoteEvent.class,
                EndOfRoundBlockEvent.class,
                OpeningBalanceEvent.class,
                FeesEvent.class,
                ExchangeRateEvent.class,
                ServiceNodesEvent.class,
                BlockSubscriptionQuery.class,
                ApplicationMessageEvent.class,
                CommandFailedEvent.class,
                QueryFailedResponse.class,
                CreateNewAddressCommand.class,
                ClusterTransferStep1Command.class,
                ClusterTransferStep2Command.class,
                ClusterTransferStep3Command.class,
                ClustersStatusQuery.class,
                CreateNewAddressEvent.class,
                ClusterTransferStep3Event.class,
                ClustersStatusResponse.class,
                TransferValueCommand.class,
                SubscriptionQuery.class,
                CurrentBalanceQuery.class,
                ExchangeRateQuery.class,
                ClusterStatusQuery.class,
                TransferValueEvent.class,
                SubscriptionSuccessResponse.class,
                CurrentBalanceResponse.class,
                ExchangeRateResponse.class,
                ClusterStatusResponse.class,
                DepositValueCommand.class,
                WithdrawValueCommand.class,
                NewOrderCommand.class,
                CancelOrderCommand.class,
                DepositValueEvent.class,
                WithdrawValueEvent.class,
                ExecutionReportEvent.class
        );
    }

    private transient Bytes<ByteBuffer> sigAndMsg;
    private long sourceAddress;
    private long eventTime;

    protected SignedMessage() {
    }

    protected SignedMessage(long sourceAddress, long eventTime) {
        this.sourceAddress = sourceAddress;
        this.eventTime = eventTime;
    }

    public static void addAliases() {
        // initialised in static block
    }

    @Override
    public final void readMarshallable(BytesIn bytes) throws IORuntimeException {
        sigAndMsg().clear().write((BytesStore) bytes);
        bytes.readSkip(Ed25519.SIGNATURE_LENGTH);
        sourceAddress = bytes.readLong();
        eventTime = bytes.readLong();
        int protocol = bytes.readUnsignedByte();
        assert bytes.lenient() || protocol == 1;
        int messageType = bytes.readUnsignedByte();
        assert bytes.lenient() || messageType == messageType();
        int padding = bytes.readUnsignedShort();
        readMarshallable2(bytes);
    }

    protected abstract void readMarshallable2(BytesIn<?> bytes);

    @Override
    public final void writeMarshallable(BytesOut bytes) {
        if (hasSignature()) {
            bytes.write(sigAndMsg);
            return;
        }
        // todo should never need to write without a signature in production.
        for (int i = 0; i < Ed25519.SIGNATURE_LENGTH; i += 8)
            bytes.writeLong(0L);
        bytes.writeLong(sourceAddress);
        bytes.writeLong(eventTime);
        bytes.writeUnsignedByte(1);
        bytes.writeUnsignedByte(messageType());
        bytes.writeUnsignedShort(0); // padding.
        writeMarshallable2(bytes);
    }

    public void sign(Bytes tempBytes, long sourceAddress, Bytes secretKey) {
        if (this.sourceAddress == 0)
            this.sourceAddress = sourceAddress;
        else if (this.sourceAddress != sourceAddress)
            throw new IllegalArgumentException("Cannot change the source address");
        tempBytes.clear();
        tempBytes.writeLong(sourceAddress);
        tempBytes.writeLong(eventTime);
        tempBytes.writeUnsignedByte(1);
        tempBytes.writeUnsignedByte(messageType());
        tempBytes.writeUnsignedShort(0); // padding.
        writeMarshallable2(tempBytes);
        if (sigAndMsg == null)
            sigAndMsg = Bytes.elasticByteBuffer();
        else
            sigAndMsg.clear();
        Ed25519.sign(sigAndMsg, tempBytes, secretKey);
    }

    @Override
    public <T extends Marshallable> T copyTo(@NotNull T t) {
        SignedMessage sm = (SignedMessage) t;
        if (sigAndMsg == null) {
            if (sm.sigAndMsg != null)
                sm.sigAndMsg.clear();
        } else {
            if (sm.sigAndMsg == null)
                sm.sigAndMsg = Bytes.elasticByteBuffer((int) sigAndMsg.readRemaining());
            sm.sigAndMsg.clear().write(sigAndMsg);
        }
        sm.sourceAddress(sourceAddress);
        sm.eventTime(eventTime);
        return t;
    }

    public abstract int messageType();

    protected abstract void writeMarshallable2(BytesOut<?> bytes);

    @Override
    public void reset() {
        sigAndMsg().clear();
        sourceAddress = 0;
        eventTime = 0;
    }

    public boolean hasSignature() {
        return sigAndMsg != null && sigAndMsg.readRemaining() >= Ed25519.SIGNATURE_LENGTH;
    }

    public Bytes<ByteBuffer> sigAndMsg() {
        return sigAndMsg == null ? sigAndMsg = Bytes.elasticByteBuffer() : sigAndMsg;
    }

    public SignedMessage sigAndMsg(Bytes<ByteBuffer> sigAndMsg) {
        this.sigAndMsg = sigAndMsg;
        return this;
    }

    public long sourceAddress() {
        assert sourceAddress != 0;
        return sourceAddress;
    }

    public SignedMessage sourceAddress(long sourceAddress) {
        assert sourceAddress != 0;
        this.sourceAddress = sourceAddress;
        return this;
    }

    public long eventTime() {
        return eventTime;
    }

    public SignedMessage eventTime(long eventTime) {
        this.eventTime = eventTime;
        return this;
    }
}
