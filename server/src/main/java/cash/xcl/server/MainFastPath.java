package cash.xcl.server;

import cash.xcl.api.dto.*;
import cash.xcl.api.exch.DepositValueCommand;
import cash.xcl.api.exch.WithdrawValueCommand;
import cash.xcl.api.util.AbstractAllMessages;
import net.openhft.chronicle.core.annotation.NotNull;

public class MainFastPath extends AbstractAllMessages {
    private final Chainer chainer;
    private final AddressService addressService;

    public MainFastPath(long address, Chainer chainer, AddressService addressService) {
        super(address);
        this.chainer = chainer;
        this.addressService = addressService;
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        createNewAddressCommand.newAddressSeed(addressService.generateAddress(createNewAddressCommand.regionStr()));
        // TODO validate
        chainer.createNewAddressCommand(createNewAddressCommand);
    }


    // TODO do we need a LocalFastPath ?

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        // TODO validate
        chainer.openingBalanceEvent(openingBalanceEvent);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        // TODO validate
        chainer.transferValueCommand(transferValueCommand);
    }


//    @Override
//    public void depositValueCommand(DepositValueCommand depositValueCommand) {
//        // TODO validate
//        chainer.depositValueCommand(depositValueCommand);
//    }
//
//    @Override
//    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
//        // TODO validate
//        chainer.withdrawValueCommand(withdrawValueCommand);
//    }

    @Override
    public void currentBalanceQuery(@NotNull final CurrentBalanceQuery currentBalanceQuery) {
        // TODO validate
        chainer.currentBalanceQuery(currentBalanceQuery);
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        // TODO validate
        chainer.exchangeRateQuery(exchangeRateQuery);
    }


    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        // TODO validate
        chainer.clusterTransferStep1Command(clusterTransferStep1Command);
    }


}
