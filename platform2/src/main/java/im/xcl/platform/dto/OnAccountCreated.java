package im.xcl.platform.dto;

public class OnAccountCreated extends VanillaSignedMessage {
    private CreateAccount createAccount;

    public OnAccountCreated(int protocol, int messageType) {
        super(protocol, messageType);
    }

    public CreateAccount createAccount() {
        return createAccount;
    }

    public OnAccountCreated createAccount(CreateAccount createAccount) {
        assert !signed();
        this.createAccount = createAccount;
        return this;
    }
}
