package cash.xcl.api;

import cash.xcl.api.dto.NewAddressRejectedEvent;

public interface ServerOut {
    void newAddressRejectedEvent(NewAddressRejectedEvent newAddressRejectedEvent);
}
