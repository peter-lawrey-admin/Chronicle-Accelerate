package im.xcl.platform.api;

import im.xcl.platform.dto.ApplicationError;
import im.xcl.platform.dto.CreateAccount;
import im.xcl.platform.dto.OnAccountCreated;
import im.xcl.platform.dto.Verification;

public interface SystemMessageListener {
    void createAccount(CreateAccount createAccount);

    void onAccountCreated(OnAccountCreated onAccountCreated);

    void verification(Verification verification);

    void applicatioNError(ApplicationError applicationError);
}
