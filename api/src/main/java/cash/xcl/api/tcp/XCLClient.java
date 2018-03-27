package cash.xcl.api.tcp;

import cash.xcl.api.AllMessages;
import cash.xcl.api.DtoParser;
import cash.xcl.api.dto.BaseDtoParser;
import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.dto.SubscriptionQuery;
import cash.xcl.api.util.XCLBase32;
import cash.xcl.net.TCPClientListener;
import cash.xcl.net.TCPConnection;
import cash.xcl.net.VanillaTCPClient;
import cash.xcl.util.XCLLongObjMap;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.core.io.IORuntimeException;
import net.openhft.chronicle.core.time.SystemTimeProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class XCLClient extends WritingAllMessages implements Closeable, TCPConnection {
    final ThreadLocal<Bytes> bytesTL = ThreadLocal.withInitial(Bytes::allocateElasticDirect);
    private final VanillaTCPClient tcpClient;
    private final long address;
    private final AllMessages allMessageListener;
    private final XCLLongObjMap<BytesStore> addressToPrivateKey = XCLLongObjMap.withExpectedSize(BytesStore.class, 16);
    private boolean internal = false;

    public XCLClient(String name,
                     String socketHost,
                     int socketPort,
                     long address,
                     Bytes secretKey,
                     AllMessages allMessageListener) {
        this(name, Arrays.asList(new InetSocketAddress(socketHost, socketPort)), address, secretKey, allMessageListener);
    }

    public XCLClient(String name,
                     String socketHost,
                     int socketPort,
                     long address,
                     Bytes secretKey,
                     AllMessages allMessageListener,
                     boolean subscribe) {
        this(name, Arrays.asList(new InetSocketAddress(socketHost, socketPort)), address, secretKey, allMessageListener);
        if (subscribe)
            subscriptionQuery(new SubscriptionQuery(address, SystemTimeProvider.INSTANCE.currentTimeMicros()));
    }


    public XCLClient(String name,
                     List<InetSocketAddress> socketAddresses,
                     long address,
                     Bytes secretKey,
                     AllMessages allMessageListener) {
        this.address = address;
        this.allMessageListener = allMessageListener;
        this.tcpClient = new VanillaTCPClient(name, socketAddresses, new ClientListener());
        register(address, secretKey);
    }

    @Override
    public WritingAllMessages to(long addressOrRegion) {
        if (addressOrRegion != address)
            throw new IllegalArgumentException("Address " + XCLBase32.encode(addressOrRegion) + " needs to be " + XCLBase32.encode(address));
        return this;
    }

    public void register(long address, Bytes secretKey) {
        addressToPrivateKey.put(address, secretKey.copy());
    }

    @Override
    public void write(SignedMessage message) {
        try {
            if (!message.hasSignature()) {
                if (message.eventTime() == 0)
                    message.eventTime(SystemTimeProvider.INSTANCE.currentTimeMicros());
                Bytes bytes = bytesTL.get();
                bytes.clear();
                long msgAddr = message.sourceAddress();
                if (msgAddr == 0) msgAddr = address;
                BytesStore secretKey = addressToPrivateKey.get(msgAddr);
                if (!internal && secretKey == null)
                    throw new IllegalArgumentException("Address " + XCLBase32.encode(msgAddr) + " not registered");
                message.sign(bytes, address, secretKey);
            }
            tcpClient.write(message.sigAndMsg());

        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void write(Bytes<ByteBuffer> bytes) throws IOException {
        tcpClient.write(bytes);
    }

    @Override
    public void close() {
        tcpClient.close();
    }

    public boolean internal() {
        return internal;
    }

    public XCLClient internal(boolean internal) {
        this.internal = internal;
        return this;
    }

    class ClientListener implements TCPClientListener {
        final DtoParser parser = new BaseDtoParser();

        @Override
        public void onMessage(TCPConnection client, Bytes bytes) throws IOException {
            try {
                parser.parseOne(bytes, allMessageListener);

            } catch (IORuntimeException iore) {
                if (iore.getCause() instanceof IOException)
                    throw (IOException) iore.getCause();
                throw iore;
            }
        }
    }
}
