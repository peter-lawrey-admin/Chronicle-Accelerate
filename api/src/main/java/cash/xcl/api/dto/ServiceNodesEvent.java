package cash.xcl.api.dto;

import net.openhft.chronicle.bytes.BytesIn;
import net.openhft.chronicle.bytes.BytesOut;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceNodesEvent extends SignedMessage {
    private String region; // or service
    private Map<Long, String> addressToIP = new LinkedHashMap<>();

    public ServiceNodesEvent() {
    }

    public ServiceNodesEvent(long sourceAddress, long eventTime) {
        super(sourceAddress, eventTime);
    }

    @Override
    protected void readMarshallable2(BytesIn<?> bytes) {
        region = bytes.readUtf8();
        int entries = (int) bytes.readStopBit();
        if (addressToIP == null) addressToIP = new LinkedHashMap<>();
        addressToIP.clear();
        for (int i = 0; i < entries; i++) {
            addressToIP.put(bytes.readLong(), bytes.readUtf8());
        }
    }

    @Override
    public int messageType() {
        return MessageTypes.SERVICE_NODES_EVENT;
    }

    @Override
    protected void writeMarshallable2(BytesOut<?> bytes) {
        bytes.writeUtf8(region);
        bytes.writeStopBit(addressToIP.size());
        for (Map.Entry<Long, String> entry : addressToIP.entrySet()) {
            bytes.writeLong(entry.getKey()).writeUtf8(entry.getValue());
        }
    }
}
