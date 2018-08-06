package im.xcl.platform.api;

public class CreateAccount extends SelfSignedMessage<CreateAccount> {

    public CreateAccount(int protocol, int messageType) {
        super(protocol, messageType);
    }
}
