package im.xcl.platform.api;

public interface MessageRouter<T> {
    long DEFAULT_CONNECTION = 0L;

    T to(long address);
}
