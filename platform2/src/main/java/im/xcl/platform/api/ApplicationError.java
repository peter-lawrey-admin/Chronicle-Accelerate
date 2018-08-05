package im.xcl.platform.api;

public class ApplicationError extends VanillaSignedMessage<ApplicationError> {
    private VanillaSignedMessage origMessage;
    private String reason;

    public ApplicationError(short protocol, short messageType) {
        super(protocol, messageType);
    }

    public String reason() {
        return reason;
    }

    public ApplicationError reason(String reason) {
        assert !signed();
        this.reason = reason;
        return this;
    }

    public ApplicationError init(VanillaSignedMessage origMessage, String reason) {
        assert !signed();
        this.origMessage = origMessage;
        this.reason = reason;
        return this;
    }

    public VanillaSignedMessage origMessage() {
        return origMessage;
    }
}
