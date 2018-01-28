package cash.xcl.api;

import cash.xcl.api.dto.AddressInformationEvent;
import cash.xcl.api.dto.ExchangeRateEvent;
import cash.xcl.api.dto.OpeningBalanceEvent;

public interface WeeklyEvents {
    void addressInformationEvent(AddressInformationEvent addressInformationEvent);

    void exchangeRateEvent(ExchangeRateEvent exchangeRateEvent);

    void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent);

}
