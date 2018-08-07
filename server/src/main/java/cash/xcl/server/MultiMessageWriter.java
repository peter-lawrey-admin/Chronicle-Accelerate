package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.core.Maths;
import net.openhft.chronicle.core.io.Closeable;

public class MultiMessageWriter implements MessageWriter {
    final SingleMessageWriter[] messageWriters;
    private final int mask;

    public MultiMessageWriter(int count, XCLServer xclServer) {
        count = Maths.nextPower2(count, 2);
        this.mask = count - 1;
        this.messageWriters = new SingleMessageWriter[count];
        for (int i = 0; i < count; i++)
            messageWriters[i] = new SingleMessageWriter(xclServer);
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        return messageWriters[(int) (Maths.agitate(addressOrRegion) & mask)].to(addressOrRegion);
    }

    @Override
    public Runnable[] runnables() {
        return messageWriters;
    }

    @Override
    public void close() {
        Closeable.closeQuietly((Object[]) messageWriters);
    }
}
