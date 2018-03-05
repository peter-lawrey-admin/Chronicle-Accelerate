package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.api.util.CountryRegion;
import cash.xcl.api.util.XCLBase32;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockEngine extends AbstractAllMessages {
    private final int region;
    private final int periodMS;

    private final AllMessagesServer fastPath;
    private final Chainer chainer;
    private final Gossiper gossiper;
    private final Voter voter;
    private final VoteTaker voteTaker;
    private final BlockReplayer blockReplayer;
    private final AllMessagesServer postBlockChainProcessor;

    private final TransactionBlockGossipEvent tbge;
    private final ExecutorService votingSes;
    private final ExecutorService processingSes;
    private final ExecutorService writerSes;
    private final long[] clusterAddresses;
    long blockNumber = 0;
    private long nextSend;
    private MessageWriter messageWriter;

    public BlockEngine(long address,
                       int region,
                       int periodMS,
                       AllMessagesServer fastPath,
                       Chainer chainer,
                       AllMessagesServer postBlockChainProcessor,
                       long[] clusterAddresses) {
        super(address);
        this.region = region;
        this.periodMS = periodMS;
        this.fastPath = fastPath;
        this.chainer = chainer;
        this.postBlockChainProcessor = postBlockChainProcessor;
        tbge = new TransactionBlockGossipEvent();
        nextSend = ((System.currentTimeMillis() / periodMS) * periodMS) + periodMS;
        this.clusterAddresses = clusterAddresses;
        gossiper = new VanillaGossiper(address, region, clusterAddresses);
        voter = new VanillaVoter(address, region, clusterAddresses);
        voteTaker = new VanillaVoteTaker(address, region, clusterAddresses);
        blockReplayer = new VanillaBlockReplayer(address, postBlockChainProcessor);
        String regionStr = XCLBase32.encodeIntUpper(region);
        votingSes = Executors.newSingleThreadExecutor(new NamedThreadFactory(regionStr + "-voter", true));
        processingSes = Executors.newSingleThreadExecutor(new NamedThreadFactory(regionStr + "-processor", true));
        writerSes = Executors.newSingleThreadExecutor(new NamedThreadFactory(regionStr + "-writer", true));
    }

    public static BlockEngine newMain(long address, int periodMS, long[] clusterAddresses) {
        final AddressService addressService = new AddressService();

        Chainer chainer = new QueuingChainer(CountryRegion.MAIN_CHAIN);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, addressService);

        AllMessagesServer postBlockChainProcessor = new MainPostBlockChainProcessor(address, addressService);
        return new BlockEngine(address, CountryRegion.MAIN_CHAIN, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public static BlockEngine newLocal(long address, int region, int periodMS, long[] clusterAddresses) {
        Chainer chainer = new VanillaChainer(region);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, null);

        AllMessagesServer postBlockChainProcessor = new LocalPostBlockChainProcessor(address);
        return new BlockEngine(address, region, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public void start() {
        votingSes.submit(this::runVoter);
        writerSes.submit(messageWriter);
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        super.allMessagesLookup(lookup);
        fastPath.allMessagesLookup(this);
        gossiper.allMessagesLookup(this);
        voter.allMessagesLookup(this);
        voteTaker.allMessagesLookup(this);
        messageWriter = new MessageWriter((XCLServer) lookup);
        postBlockChainProcessor.allMessagesLookup(messageWriter);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        fastPath.createNewAddressCommand(createNewAddressCommand);
    }

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        fastPath.openingBalanceEvent(openingBalanceEvent);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        fastPath.transferValueCommand(transferValueCommand);
    }

    @Override
    public void clusterTransferStep1Command(ClusterTransferStep1Command clusterTransferStep1Command) {
        fastPath.clusterTransferStep1Command(clusterTransferStep1Command);
    }


    @Override
    public void transactionBlockEvent(TransactionBlockEvent transactionBlockEvent) {
        blockReplayer.transactionBlockEvent(transactionBlockEvent);
        gossiper.transactionBlockEvent(transactionBlockEvent);
    }

    @Override
    public void transactionBlockGossipEvent(TransactionBlockGossipEvent transactionBlockGossipEvent) {
        voter.transactionBlockGossipEvent(transactionBlockGossipEvent);
    }

    @Override
    public void transactionBlockVoteEvent(TransactionBlockVoteEvent transactionBlockVoteEvent) {
        voteTaker.transactionBlockVoteEvent(transactionBlockVoteEvent);
    }

    @Override
    public void endOfRoundBlockEvent(EndOfRoundBlockEvent endOfRoundBlockEvent) {
        blockReplayer.treeBlockEvent(endOfRoundBlockEvent);
    }

    void runVoter() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                TransactionBlockEvent tbe = chainer.nextTransactionBlockEvent();
                // tg System.out.println("TBE "+tbe);
                if (tbe != null) {
                    tbe.sourceAddress(address);
                    tbe.blockNumber(blockNumber++);
                    for (long clusterAddress : clusterAddresses) {
                        to(clusterAddress).transactionBlockEvent(tbe);
                    }
                }

                int subRound = 1; //Math.max(1, periodMS / 10);
                Jvm.pause(subRound);
                gossiper.sendGossip(blockNumber);
                Jvm.pause(subRound);
                voter.sendVote(blockNumber);
                Jvm.pause(subRound);
                //System.out.println(address + " " + blockNumber);
                if (voteTaker.hasMajority()) {
                    voteTaker.sendEndOfRoundBlock(blockNumber++);
                }

                processingSes.submit(blockReplayer::replayBlocks);
                nextSend += periodMS;
                long delay = nextSend - SystemTimeProvider.INSTANCE.currentTimeMillis();
                if (delay > 1)
                    Jvm.pause(delay);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void close() {
        votingSes.shutdownNow();
    }

    // only for testing purposes
    public void printBalances() {
        if (postBlockChainProcessor instanceof LocalPostBlockChainProcessor) {
            ((LocalPostBlockChainProcessor) postBlockChainProcessor).printBalances();
        }
    }
}
