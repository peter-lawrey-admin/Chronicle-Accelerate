package cash.xcl.server;

import cash.xcl.api.dto.SignedMessage;
import cash.xcl.api.tcp.WritingAllMessages;

import java.util.concurrent.atomic.AtomicInteger;

class MyWritingAllMessages extends WritingAllMessages {
    private final AtomicInteger count;

    public MyWritingAllMessages(AtomicInteger count) {
        this.count = count;
    }

    @Override
    public WritingAllMessages to(long addressOrRegion) {
        return this;
    }

    @Override
    public void write(SignedMessage message) {
        count.incrementAndGet();
    }

    @Override
    public void close() {

    }
}
