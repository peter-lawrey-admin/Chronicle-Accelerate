package cash.xcl.api;

import cash.xcl.api.dto.SignedBinaryMessage;

public class ClientException extends RuntimeException {
    private SignedBinaryMessage message;

    public ClientException(SignedBinaryMessage message) {
        this.message = message;
    }

    public SignedBinaryMessage message() {
        return message;
    }
}
