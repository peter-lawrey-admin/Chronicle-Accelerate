package cash.xcl.api;

import cash.xcl.api.dto.SignedMessage;

public interface SignedMessageConsumer {
    void write(SignedMessage message);
}
