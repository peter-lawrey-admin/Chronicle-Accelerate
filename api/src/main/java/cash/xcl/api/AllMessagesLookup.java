package cash.xcl.api;

@FunctionalInterface
public interface AllMessagesLookup {
    AllMessages to(long addressOrRegion);
}
