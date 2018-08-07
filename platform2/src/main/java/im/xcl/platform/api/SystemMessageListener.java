package im.xcl.platform.api;

public interface SystemMessageListener {
    void createAccount(CreateAccount createAccount);

    void onAccountCreated(OnAccountCreated onAccountCreated);

    void verification(Verification verification);

    void applicatioNError(ApplicationError applicationError);
}
