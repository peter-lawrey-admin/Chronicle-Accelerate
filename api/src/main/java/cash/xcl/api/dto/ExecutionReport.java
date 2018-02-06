package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.wire.AbstractBytesMarshallable;

// FIXME needs reviewing/completing
public class ExecutionReport extends AbstractBytesMarshallable {

    private final String symbol1Symbol2;
    private final double price;
    private final double quantity;
    private Side side;

    public ExecutionReport(String symbol1Symbol2,
                           Side side,
                           double price,
                           double quantity) {
        this.symbol1Symbol2 = symbol1Symbol2;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public void readMarshallable(BytesIn bytes) throws IORuntimeException {

        // TODO

    }

    @Override
    public void writeMarshallable(BytesOut bytes) {

        // TODO

    }

}
