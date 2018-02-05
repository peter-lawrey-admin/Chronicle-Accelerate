package cash.xcl.api.dto;

/**
 * This must be up to date with Chronicle-Accelerate/rfc/XCLBlockChain.adoc
 * <p>
 * Commands are an instruction to change something.
 * These can trigger an event if successful or a COMMAND_FAILED_EVENT.
 * <p>
 * Events are notification of something which has happened.
 * These could trigger further events or an APPLICATION_MESSAGE_EVENT if it fails or reports a warning.
 * <p>
 * Queries are requests for information. These are not recorded on the blockchain.
 * These could trigger a replay of an event, produce a response, or return a QUERY_FAILED_RESPONSE.
 * <p>
 * Response are messages returned from a successful query.
 */
public interface MethodIds {
    // bootstrap events
    int TRANSACTION_BLOCK_EVENT = 0x01;
    int TREE_BLOCK_EVENT = 0x02;
    int OPENING_BALANCE_EVENT = 0x03;
    int FEES_EVENT = 0x04;
    int EXCHANGE_RATE_EVENT = 0x05;

    // runtime events
    int APPLICATION_MESSAGE_EVENT = 0x10;
    int COMMAND_FAILED_EVENT = 0x11;
    int QUERY_FAILED_RESPONSE = 0x12;

    // Main chain commands and queries
    int CREATE_NEW_ADDRESS_COMMAND = 0x20;
    int CLUSTER_TRANSFER_STEP1_COMMAND = 0x21;
    int CLUSTER_TRANSFER_STEP2_COMMAND = 0x22;
    int CLUSTER_TRANSFER_STEP3_COMMAND = 0x23;

    int CLUSTERS_STATUS_QUERY = 0x2f;

    // Main chain events
    int CREATE_NEW_ADDRESS_EVENT = 0x30;
    int CLUSTER_TRANSFER_STEP3_EVENT = 0x33;

    int CLUSTERS_STATUS_RESPONSE = 0x3f;

    // Regional commands and queries
    int TRANSFER_VALUE_COMMAND = 0x40;

    int SUBSCRIPTION_QUERY = 0x4c;
    int CURRENT_BALANCE_QUERY = 0x4d;
    int EXCHANGE_RATE_QUERY = 0x4e;
    int CLUSTER_STATUS_QUERY = 0x4f;

    // Regional events and responses
    int TRANSFER_VALUE_EVENT = 0x50;

    int SUBSCRIPTION_SUCCESS_EVENT = 0x5c;
    int CURRENT_BALANCE_RESPONSE = 0x5d;
    int EXCHANGE_RATE_RESPONSE = 0x5e;
    int CLUSTER_STATUS_RESPONSE = 0x5f;

    // Exchange service commands and queries
    int DEPOSIT_VALUE_COMMAND = 0x60;
    int WITHDRAW_VALUE_COMMAND = 0x61;
    int NEW_MARKET_ORDER_COMMAND = 0x62;
    int NEW_LIMIT_ORDER_COMMAND = 0x63;
    int CANCEL_ORDER_COMMAND = 0x64;

    // Exchange service events and responses
    int DEPOSIT_VALUE_EVENT = 0x70;
    int WITHDRAW_VALUE_EVENT = 0x71;

    int EXECUTION_REPORT = 0x72;
}
