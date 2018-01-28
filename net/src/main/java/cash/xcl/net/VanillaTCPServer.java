package cash.xcl.net;

import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VanillaTCPServer implements TCPServer {
    final ServerSocketChannel serverChannel;
    private final ExecutorService pool;
    private final List<TCPConnection> connections = Collections.synchronizedList(new ArrayList<>());
    private final TCPServerConnectionListener connectionListener;
    volatile boolean running = true;

    public VanillaTCPServer(String name, int port, TCPServerConnectionListener connectionListener) throws IOException {
        this.connectionListener = connectionListener;
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        pool = Executors.newCachedThreadPool(new NamedThreadFactory(name, false));
        pool.submit(this::run);
    }

    private void run() {
        try {
            while (running) {
                SocketChannel accept = serverChannel.accept();
                pool.submit(new VanillaTCPServerConnection(this, accept)::run);
            }
        } catch (Throwable t) {
            if (running)
                t.printStackTrace();
            close();
        }
    }

    @Override
    public void close() {
        running = false;
        pool.shutdown();

        Closeable.closeQuietly(connections);
        Closeable.closeQuietly(serverChannel);
    }

    @Override
    public TCPServerConnectionListener connectionListener() {
        return connectionListener;
    }
}
