package cash.xcl.api.dto;

public interface MethodIds {
    int TRANSACTION_BLOCK_EVENT = 0x01;
    int TREE_BLOCK_EVENT = 0x02;
    int CREATE_NEW_ADDRESS_COMMAND = 0x20;
    int ADDRESS_INFORMATION_EVENT = 0x30;
    int EXCHANGE_RATE_EVENT = 0x31;
    int NEW_ADDRESS_REJECTED = 0x31;
    int OPENING_BALANCE_EVENT = 0x50;
    int TRANSFER_COMMAND = 0x51;
    int NEW_MARKET_ORDER = 0x60;
    int NEW_LIMIT_ORDER = 0x61;
    int CANCEL_ORDER = 0x62;
    int EXECUTION_REPORT = 0x70;

}
