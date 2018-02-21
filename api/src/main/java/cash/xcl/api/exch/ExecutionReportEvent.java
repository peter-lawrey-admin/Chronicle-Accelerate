package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

// FIXME needs reviewing/completing
public class ExecutionReportEvent extends SignedMessage {

    private ExecutionReport executionReport;

    public ExecutionReportEvent(long sourceAddress, long eventTime, ExecutionReport executionReport) {
        super(sourceAddress, eventTime);
        this.executionReport = executionReport;
    }

    public ExecutionReportEvent() {
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        //executionReport.readMarshallable(bytes);
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        executionReport.writeMarshallable(bytes);
    }

    @Override
    public int messageType() {
        return MessageTypes.EXECUTION_REPORT;
    }

}
