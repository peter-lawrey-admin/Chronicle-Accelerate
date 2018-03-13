package cash.xcl.api.exch.fix;

/**
 * Generated at software.chronicle.fix.codegen.MessageParserGenerator.generateManifest(MessageParserGenerator.java)
 */
public interface MessageManifest {
    char Heartbeat = '0';

    char Logon = 'A';

    char TestRequest = '1';

    char ResendRequest = '2';

    char Reject = '3';

    char SequenceReset = '4';

    char Logout = '5';

    char NewOrderSingle = 'D';

    char ExecutionReport = '8';

    char MarketDataSnapshotFullRefresh = 'W';
}
