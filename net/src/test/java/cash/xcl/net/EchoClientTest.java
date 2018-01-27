package cash.xcl.net;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class EchoClientTest {
    public static void main(String[] args) throws IOException {
        AtomicInteger counter = new AtomicInteger();
        TCPClientListener clientListener = (client, bytes) -> {
            System.out.println(bytes);
            counter.decrementAndGet();
        };
        VanillaTCPClient client = new VanillaTCPClient("echo", Arrays.asList(new InetSocketAddress("localhost", EchoServer.PORT)), clientListener);

        Bytes<ByteBuffer> bytes = Bytes.elasticByteBuffer();
        for (int i = 0; i < 100; i++) {
            bytes.clear().append("Hello ").append(i);
            counter.getAndIncrement();
            client.write(bytes);
        }
        while (counter.get() > 0)
            Jvm.pause(50);
        client.close();
    }
}
