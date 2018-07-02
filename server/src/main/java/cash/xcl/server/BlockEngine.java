package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.api.util.CountryRegion;
import cash.xcl.util.XCLBase32;
import net.openhft.chronicle.core.annotation.NotNull;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

public class BlockEngine extends AbstractAllMessages {
    private final int region;
    private final int periodUS;

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
    private long nextSendUS;
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
        this.periodUS = periodMS * 1000;
        this.fastPath = fastPath;
        this.chainer = chainer;
        this.postBlockChainProcessor = postBlockChainProcessor;
        tbge = new TransactionBlockGossipEvent();
        nextSendUS = (SystemTimeProvider.INSTANCE.currentTimeMicros() / periodUS + 1) * periodUS;
        this.clusterAddresses = clusterAddresses;
        gossiper = new VanillaGossiper(address, region, clusterAddresses);
        voter = new VanillaVoter(address, region, clusterAddresses);
        voteTaker = new VanillaVoteTaker(address, region, clusterAddresses);
        blockReplayer = new VanillaBlockReplayer(address, postBlockChainProcessor);
        String regionStr = XCLBase32.encodeIntUpper(region);
        votingSes = Executors.newSingleThreadExecutor(new NamedThreadFactory(regionStr + "-voter", true, Thread.MAX_PRIORITY));
        processingSes = Executors.newSingleThreadExecutor(new NamedThreadFactory(regionStr + "-processor", true, Thread.MAX_PRIORITY));
        writerSes = Executors.newCachedThreadPool(new NamedThreadFactory(regionStr + "-writer", true, Thread.MIN_PRIORITY));
    }

    public static BlockEngine newMain(long address, int periodMS, long[] clusterAddresses) {
        assert LongStream.of(clusterAddresses).distinct().count() == clusterAddresses.length;

        final AddressService addressService = new AddressService();

        Chainer chainer = new QueuingChainer(CountryRegion.MAIN_CHAIN);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, addressService);

        AllMessagesServer postBlockChainProcessor = new MainPostBlockChainProcessor(address, addressService);
        return new BlockEngine(address, CountryRegion.MAIN_CHAIN, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public static BlockEngine newLocal(long address, int region, int periodMS, long[] clusterAddresses, long tbeInitialCapacity) {
        assert LongStream.of(clusterAddresses).distinct().count() == clusterAddresses.length;

        Chainer chainer = new VanillaChainer(region, tbeInitialCapacity);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, null);

        AllMessagesServer postBlockChainProcessor = new LocalPostBlockChainProcessor(address);
        return new BlockEngine(address, region, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public void start() {
        votingSes.submit(this::runVoter);
        for (Runnable runnable : messageWriter.runnables()) {
            writerSes.submit(runnable);
        }
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        super.allMessagesLookup(lookup);
        fastPath.allMessagesLookup(this);
        gossiper.allMessagesLookup(this);
        voter.allMessagesLookup(this);
        voteTaker.allMessagesLookup(this);
        messageWriter = new MultiMessageWriter(2, (XCLServer) lookup);
//        messageWriter = new SingleMessageWriter((XCLServer) lookup);
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

//    @Override
//    public void depositValueCommand(DepositValueCommand depositValueCommand) {
//        fastPath.depositValueCommand(depositValueCommand);
//    }
//
//    @Override
//    public void withdrawValueCommand(WithdrawValueCommand withdrawValueCommand) {
//        fastPath.withdrawValueCommand(withdrawValueCommand);
//    }

    @Override
    public void currentBalanceQuery(@NotNull final CurrentBalanceQuery currentBalanceQuery) {
        fastPath.currentBalanceQuery(currentBalanceQuery);
    }

    @Override
    public void exchangeRateQuery(ExchangeRateQuery exchangeRateQuery) {
        fastPath.exchangeRateQuery(exchangeRateQuery);
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
//            Histogram one = new Histogram();
//            Histogram two = new Histogram();
//            Histogram three = new Histogram();
//            Histogram four = new Histogram();
            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                long start = System.nanoTime();
                TransactionBlockEvent tbe = chainer.nextTransactionBlockEvent();
//                one.sample(System.nanoTime() - start);
                start = System.nanoTime();
                // tg System.out.println("TBE "+tbe);
                if (tbe != null) {
                    tbe.sourceAddress(address);
                    tbe.blockNumber(blockNumber++);
                    for (long clusterAddress : clusterAddresses) {
                        to(clusterAddress)
                                .transactionBlockEvent(tbe);
                    }
                }

//                int subRound = Math.max(100_000, periodUS * 100);
//                two.sample(System.nanoTime() - start);
                Thread.sleep(1);
//                LockSupport.parkNanos(subRound);
                gossiper.sendGossip(blockNumber);
                Thread.sleep(1);
//                LockSupport.parkNanos(subRound);

                start = System.nanoTime();
                voter.sendVote(blockNumber);
//                three.sample(System.nanoTime() - start);

                Thread.sleep(1);
//                LockSupport.parkNanos(subRound);
                start = System.nanoTime();
                //System.out.println(address + " " + blockNumber);
                if (voteTaker.hasMajority()) {
                    voteTaker.sendEndOfRoundBlock(blockNumber++);
                }

                processingSes.submit(blockReplayer::replayBlocks);
                nextSendUS += periodUS;
                long delay = nextSendUS - SystemTimeProvider.INSTANCE.currentTimeMicros();
//                four.sample(System.nanoTime() - start);

                if (delay > 999)
                    Thread.sleep(delay / 1000);
//                if (delay > 10) // minimum delay
//                    LockSupport.parkNanos(delay * 1000);

/*
                if (++count % 100 == 0) {
                    System.out.println(one.toMicrosFormat());
                    System.out.println(two.toMicrosFormat());
                    System.out.println(three.toMicrosFormat());
                    System.out.println(four.toMicrosFormat());
                }
*/

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
