package im.xcl.platform.dto;

import net.openhft.chronicle.core.pool.ClassAliasPool;

public enum DtoAlias {
    ;

    static {
        ClassAliasPool.CLASS_ALIASES.addAlias(
                ApplicationError.class,
                CreateAccount.class,
                OnAccountCreated.class,
                Verification.class
        );
    }

    public static void addAliases() {
        // static init block does everything.
    }
}
