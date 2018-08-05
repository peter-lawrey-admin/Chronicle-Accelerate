package im.xcl.platform.api;

public class OnAccountCreated extends VanillaSignedMessage {
    private CreateAccount createAccount;

    public OnAccountCreated(short protocol, short messageType) {
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
