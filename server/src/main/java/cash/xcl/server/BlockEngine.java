package cash.xcl.server;

import cash.xcl.api.AllMessagesLookup;
import cash.xcl.api.AllMessagesServer;
import cash.xcl.api.dto.*;
import cash.xcl.api.util.AbstractAllMessages;
import cash.xcl.api.util.CountryRegion;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.threads.NamedThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlockEngine extends AbstractAllMessages {
    private final String region;
    private final int periodMS;

    private final AllMessagesServer fastPath;
    private final VanillaChainer chainer;
    private final Gossiper gossiper;
    private final Voter voter;
    private final VoteTaker voteTaker;
    private final BlockReplayer blockReplayer;
    private final AllMessagesServer postBlockChainProcessor;
    private final AllMessagesServer finalRouter;

    private final TransactionBlockGossipEvent tbge;
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("chain", true));
    private final long[] clusterAddresses;
    long blockNumber = 0;
    private long nextSend;

    public BlockEngine(long address,
                       String region,
                       int periodMS,
                       AllMessagesServer fastPath,
                       VanillaChainer chainer,
                       AllMessagesServer postBlockChainProcessor,
                       long[] clusterAddresses) {
        super(address);
        this.region = region;
        this.periodMS = periodMS;
        this.fastPath = fastPath;
        this.chainer = chainer;
        this.postBlockChainProcessor = postBlockChainProcessor;
        finalRouter = new VanillaFinalRouter(address);
        tbge = new TransactionBlockGossipEvent();
        nextSend = System.currentTimeMillis() / periodMS * periodMS + periodMS;
        this.clusterAddresses = clusterAddresses;
        gossiper = new VanillaGossiper(address, region, clusterAddresses);
        voter = new VanillaVoter(address, region, clusterAddresses);
        voteTaker = new VanillaVoteTaker(address, region, clusterAddresses);
        blockReplayer = new VanillaBlockReplayer(address, postBlockChainProcessor);
    }

    public static BlockEngine newMain(long address, int periodMS, long[] clusterAddresses) {
        final AddressService addressService = new AddressService();

        VanillaChainer chainer = new VanillaChainer(CountryRegion.MAIN_NAME);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, addressService);

        AllMessagesServer postBlockChainProcessor = new MainPostBlockChainProcessor(address, addressService);
        return new BlockEngine(address, CountryRegion.MAIN_NAME, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public static BlockEngine newLocal(long address, String region, int periodMS, long[] clusterAddresses) {
        VanillaChainer chainer = new VanillaChainer(region);
        AllMessagesServer fastPath = new MainFastPath(address, chainer, null);

        AllMessagesServer postBlockChainProcessor = new LocalPostBlockChainProcessor(address);
        return new BlockEngine(address, region, periodMS, fastPath, chainer, postBlockChainProcessor, clusterAddresses);
    }

    public void start() {
        ses.submit(this::run);
    }

    @Override
    public void allMessagesLookup(AllMessagesLookup lookup) {
        super.allMessagesLookup(lookup);
        fastPath.allMessagesLookup(this);
        gossiper.allMessagesLookup(this);
        voter.allMessagesLookup(this);
        voteTaker.allMessagesLookup(this);
        postBlockChainProcessor.allMessagesLookup(this);
        finalRouter.allMessagesLookup(this);
    }

    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
        fastPath.createNewAddressCommand(createNewAddressCommand);
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
    public void treeBlockEvent(TreeBlockEvent treeBlockEvent) {
        blockReplayer.treeBlockEvent(treeBlockEvent);
    }

    void run() {
        try {
            TransactionBlockEvent tbe = chainer.nextTransactionBlockEvent();
//            System.out.println("TBE "+tbe);
            if (tbe != null) {
                tbe.sourceAddress(address);
                tbe.blockNumber(blockNumber++);
                for (long clusterAddress : clusterAddresses)
                    to(clusterAddress).transactionBlockEvent(tbe);
            }

            int subRound = Math.max(1, periodMS / 10);
            Jvm.pause(subRound);
            gossiper.sendGossip(blockNumber);
            Jvm.pause(subRound);
            voter.sendVote(blockNumber);
            Jvm.pause(subRound);
//            System.out.println(address + " " + blockNumber);
            if (voteTaker.hasMajority())
                voteTaker.sendTreeNode(blockNumber++);

            // TODO might be triggered asynchronously to improve performance.
            blockReplayer.replayBlocks();

        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            nextSend += periodMS;
            long delay = nextSend - SystemTimeProvider.INSTANCE.currentTimeMillis();
//            System.out.println("Delay "+delay);
            ses.schedule(this::run, delay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void close() {
        ses.shutdownNow();
    }

}
