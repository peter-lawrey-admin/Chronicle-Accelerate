package cash.xcl.server.mock;

import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.Gateway;
import cash.xcl.server.VanillaGateway;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;

public class ServerJVM implements Closeable{
    public static boolean INTERNAL = Boolean.getBoolean("internal");
    //public static boolean INTERNAL = true;
    public static int DEFAULT_SERVER_ADDRESS = 10001;
    public static int ITERATIONS = 3;
    public static int MAX_CLIENT_THREADS = 8;
    private XCLServer server;
    private Gateway gateway;

    public static void main(String[] args) {
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
        ServerJVM guiXclServer = null;
        try {
            guiXclServer = new ServerJVM(DEFAULT_SERVER_ADDRESS, secretKey,1000, 10, 1, publicKey);

            for (int iterationNumber = 0; iterationNumber < ITERATIONS; iterationNumber++) {
                for (int s = 0; s < MAX_CLIENT_THREADS; s++) {
                    int sourceAddress = (iterationNumber * 100) + s + 1;
                    int destinationAddress = sourceAddress + 1000000;
                    //guiXclServer.gateway.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0, sourceAddress, publicKey));
                    //guiXclServer.gateway.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0, destinationAddress, publicKey));
                    System.out.println("registering " + sourceAddress);
                    System.out.println("registering " + destinationAddress);
                    guiXclServer.register(sourceAddress, publicKey);
                    guiXclServer.register(destinationAddress, publicKey);
                }
            }

            while(true) {
                System.out.println("server is running... ");
                Thread.sleep(10000);
            }
        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
            System.out.println("exiting");
            System.exit(0);
        }
    }



    public ServerJVM(int serverAddress,
                     Bytes secretKey,
                     int mainBlockPeriodMS,
                     int localBlockPeriodMS,
                     long sourceAddress,
                     Bytes publicKey) {
        try {
            long[] clusterAddresses = {serverAddress};
            this.gateway = VanillaGateway.newGateway(serverAddress, "gb1dn", clusterAddresses, mainBlockPeriodMS, localBlockPeriodMS, TransactionBlockEvent._32_MB);
            this.server = new XCLServer("one", serverAddress, serverAddress, secretKey, gateway)
                    .internal(INTERNAL);
            gateway.start();

            register(sourceAddress, publicKey);

        } catch (Throwable t) {
            t.printStackTrace();
            close();
        }
    }

    @Override
    public void close() {
        // todo: quietly terminate the BlockEngine voting thread

        //this.gateway.close();
        Closeable.closeQuietly(this.server);
    }



    // quick workaround to register our address
    public void register(long address, Bytes<?> publicKey) {

        this.server.register(address, publicKey);
    }



    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        this.gateway.transferValueCommand(transferValueCommand);

        ((VanillaGateway) this.gateway).printBalances();
    }

    //    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        this.gateway.openingBalanceEvent(openingBalanceEvent);

        ((VanillaGateway) this.gateway).printBalances();
    }



}
