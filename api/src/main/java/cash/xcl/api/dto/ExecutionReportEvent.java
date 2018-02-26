package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
public class ExecutionReportEvent extends SignedMessage {
    private long clientAddress;
    private String clientOrderId;
    private double price;
    private double quantityFilled;
    private double quantityRemaining;


    public ExecutionReportEvent(long sourceAddress, long eventTime, long clientAddress, String clientOrderId, double price, double quantityFilled, double quantityRemaining) {
        super(sourceAddress, eventTime);
        this.clientAddress = clientAddress;
        this.clientOrderId = clientOrderId;
        this.price = price;
        this.quantityFilled = quantityFilled;
        this.quantityRemaining = quantityRemaining;
    }

    public ExecutionReportEvent() {
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        clientAddress = bytes.readLong();
        clientOrderId = bytes.readUtf8();
        price = bytes.readDouble();
        quantityFilled = bytes.readDouble();
        quantityRemaining = bytes.readDouble();
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeLong(clientAddress);
        bytes.writeUtf8(clientOrderId);
        bytes.writeDouble(price);
        bytes.writeDouble(quantityFilled);
        bytes.writeDouble(quantityRemaining);
    }

    @Override
    public int messageType() {
        return MessageTypes.EXECUTION_REPORT;
    }

    public long clientAddress() {
        return clientAddress;
    }

    public ExecutionReportEvent clientAddress(long clientAddress) {
        this.clientAddress = clientAddress;
        return this;
    }

    public String clientOrderId() {
        return clientOrderId;
    }

    public ExecutionReportEvent clientOrderId(String clientOrderId) {
        this.clientOrderId = clientOrderId;
        return this;
    }

    public double price() {
        return price;
    }

    public ExecutionReportEvent price(double price) {
        this.price = price;
        return this;
    }

    public double quantityFilled() {
        return quantityFilled;
    }

    public ExecutionReportEvent quantityFilled(double quantityFilled) {
        this.quantityFilled = quantityFilled;
        return this;
    }

    public double quantityRemaining() {
        return quantityRemaining;
    }

    public ExecutionReportEvent quantityRemaining(double quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
        return this;
    }
}
