package cash.xcl.api;

import cash.xcl.api.dto.AddressInformationEvent;
import cash.xcl.api.dto.ExchangeRateEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;
import net.openhft.chronicle.core.io.Closeable;

public interface WeeklyEvents extends Closeable {
    void addressInformationEvent(AddressInformationEvent addressInformationEvent);

    void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent);

    void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent);

}
