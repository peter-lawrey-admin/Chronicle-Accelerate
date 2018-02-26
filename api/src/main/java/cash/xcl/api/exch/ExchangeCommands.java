package cash.xcl.api.exch;

import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.DepositValueCommand;
import cash.xcl.api.dto.WithdrawValueCommand;


public interface ExchangeCommands {

    void transferToExchangeCommand(TransferToExchangeCommand transferCommand);

    void transferFromExchangeCommand(TransferFromExchangeCommand transferCommand);

    void depositValueCommand(DepositValueCommand depositValueCommand);

    void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand);

    void newLimitOrderCommand(NewLimitOrderCommand newLimitOrderCommand);

    void cancelOrderCommand(CancelOrderCommand cancelOrderCommand);

    void currentBalanceQuery(CurrentBalanceQuery currentBalanceQuery);

    // void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery); ???

}