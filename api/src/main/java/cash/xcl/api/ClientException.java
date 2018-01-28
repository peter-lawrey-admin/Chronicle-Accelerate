package cash.xcl.api;

import cash.xcl.api.dto.SignedMessage;

public class ClientException extends RuntimeException {
    private SignedMessage message;

    public ClientException(SignedMessage message) {
        this.message = message;
    }

    public SignedMessage message() {
        return message;
    }
}
