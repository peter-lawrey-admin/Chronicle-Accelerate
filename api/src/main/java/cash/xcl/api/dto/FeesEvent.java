package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

public class FeesEvent extends SignedMessage {

    //private int weekNumber;
    private double minFeePerTransaction;
    private double transactionPercentageTransaction;

    public FeesEvent(long sourceAddress, long eventTime,
                     double minFeePerTransaction,
                     double transactionPercentageTransaction) {
        super(sourceAddress, eventTime);
        this.minFeePerTransaction = minFeePerTransaction;
        this.transactionPercentageTransaction = transactionPercentageTransaction;
    }

    public FeesEvent() {
        super();
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        minFeePerTransaction = bytes.readDouble();
        transactionPercentageTransaction = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeDouble(minFeePerTransaction);
        bytes.writeDouble(transactionPercentageTransaction);
    }

    @Override
    public int messageType() {
        return MessageTypes.FEES_EVENT;
    }

}
