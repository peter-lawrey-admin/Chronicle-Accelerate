package cash.xcl.api;

@FunctionalInterface
public interface AllMessageLookup {
    AllMessages to(long addressOrRegion);
}
