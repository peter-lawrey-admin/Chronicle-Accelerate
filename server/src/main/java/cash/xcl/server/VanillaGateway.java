package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.dto.*;
import cash.xcl.api.exch.DepositValueCommand;
import cash.xcl.api.exch.WithdrawValueCommand;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.api.util.CountryRegion;
import cash.xcl.util.PublicKeyRegistry;
import cash.xcl.util.RegionIntConverter;
import net.openhft.chronicle.core.annotation.NotNull;

/**
 * This accepts message from the XCLServer and passes them to the appropriate downstream component
 */
public class VanillaGateway extends AbstractAllMessages implements Gateway {
    private final int region;
    private final BlockEngine main;
    private final BlockEngine local;
    private PublicKeyRegistry publicKeyRegistery;

    public VanillaGateway(long address, int region, BlockEngine main, BlockEngine local) {
        super(address);
        this.region = region;
        this.main = main;
        this.local = local;
    }


    public static VanillaGateway newGateway(long address, String region, long[] clusterAddresses, int mainPeriodMS, int localPeriodMS) {
        int regionInt = RegionIntConverter.INSTANCE.parse(region);
        return new VanillaGateway(address,
                regionInt,
                BlockEngine.newMain(address, mainPeriodMS, clusterAddresses),
                BlockEngine.newLocal(address, regionInt, localPeriodMS, clusterAddresses, 2 << 20)
        );
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        super.allMessagesLookup(lookup);
        main.allMessagesLookup(lookup);
        local.allMessagesLookup(lookup);
        publicKeyRegistery = (PublicKeyRegistry) lookup;
    }

    @Override
    public AllMessages to(long addressOrRegion) {
        if (addressOrRegion == 0)
            return main;
        if (addressOrRegion == (long) region << 32)
            return local;
        return super.to(addressOrRegion);
    }

    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        int region = transactionBlockEvent.region();
        if (CountryRegion.MAIN_CHAIN == region)
            main.transactionBlockEvent(transactionBlockEvent);
        else if (this.region == region)
            local.transactionBlockEvent(transactionBlockEvent);
        else
            System.err.println("Unknown region " + region);
    }

    @Override
    public void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        int region = transactionBlockGossipEvent.region();
        if (CountryRegion.MAIN_CHAIN == region)
            main.transactionBlockGossipEvent(transactionBlockGossipEvent);
        else if (this.region == region)
            local.transactionBlockGossipEvent(transactionBlockGossipEvent);
        else
            System.err.println("Unknown region " + region);
    }

    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        int region = transactionBlockVoteEvent.region();
        if (CountryRegion.MAIN_CHAIN == region)
            main.transactionBlockVoteEvent(transactionBlockVoteEvent);
        else if (this.region == region)
            local.transactionBlockVoteEvent(transactionBlockVoteEvent);
        else
            System.err.println("Unknown region " + region);
    }

    @Override
    public void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
        int region = endOfRoundBlockEvent.region();
        if (CountryRegion.MAIN_CHAIN == region)
            main.endOfRoundBlockEvent(endOfRoundBlockEvent);
        else if (this.region == region)
            local.endOfRoundBlockEvent(endOfRoundBlockEvent);
        else
            System.err.println("Unknown region " + region);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        main.createNewAddressCommand(createNewAddressCommand);
    }

    @Override
    public void createNewAddressEvent(CreateNewAddressEvent createNewAddressEvent) {
        // received as a weekly event
        checkTrusted(createNewAddressEvent);
        publicKeyRegistery.register(createNewAddressEvent.address(),
                createNewAddressEvent.publicKey());
    }

    private void checkTrusted(SignedMessage message) {

    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        // handled by the caller
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        local.transferValueCommand(transferValueCommand);
    }



    @Override
    public void currentBalanceQuery(@NotNull final CurrentBalanceQuery currentBalanceQuery) {
        local.currentBalanceQuery(currentBalanceQuery);
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        local.exchangeRateQuery(exchangeRateQuery);
    }



    // todo ?
    @Override
    public void transferValueEvent(TransferValueEvent transferValueEvent) {
        // received as a weekly event
        checkTrusted(transferValueEvent);
    }


    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        // TODO: local or main?
        local.openingBalanceEvent(openingBalanceEvent);
    }


    @Override
    public void start() {
        main.start();
        local.start();
    }

    @Override
    public void close() {
        main.close();
        local.close();
    }

    // only for testing purposes
    public void printBalances() {
        this.local.printBalances();
    }


    // TODO
    @Override
    public void depositValueCommand(DepositValueCommand depositValueCommand) {
        local.depositValueCommand(depositValueCommand);
    }

    @Override
    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
        local.withdrawValueCommand(withdrawValueCommand);
    }


}
