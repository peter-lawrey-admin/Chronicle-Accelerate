package im.xcl.platform.api;

public enum MethodIdType {
    INVALID,
    INTERNAL, // used to implement the blockchain
    TRANSACTION_COMMAND, // goes to blockchain
    TRANSACTION_RESULT, // comes from blockchain to the client.
    QUERY_REQUEST, // handled by the gateway
    QUERY_RESPONSE // response from the gateway to the client.
}
