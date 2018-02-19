package cash.xcl.api;

import cash.xcl.api.dto.*;
import net.openhft.chronicle.core.io.Closeable;

public interface WeeklyEvents extends Closeable {
    // weekly events
    void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent);

    void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent);

    void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent);

    void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent);

    void feesEvent(FeesEvent feesEvent);

    void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent);

    void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent);

    void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent);

    void currentBalanceEvent(CurrentBalanceResponse currentBalanceResponse);

    void serviceNodesEvent(ServiceNodesEvent serviceNodesEvent);

    // FIXME work in progress - move me to the correct interface

}
