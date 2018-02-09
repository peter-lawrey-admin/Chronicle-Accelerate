package cash.xcl.api;

public interface AllMessages extends ClientOut, ClientIn, ServerIn, ServerOut, WeeklyEvents {
    AllMessages to(long addressOrRegion);
}
