package cash.xcl.server.mock;

import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLServer;
import cash.xcl.server.Gateway;
import cash.xcl.server.VanillaGateway;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;




public class GuiXclServer implements Closeable{

    private XCLServer server;
    private Gateway gateway;


    public GuiXclServer(Bytes secretKey,
                        int mainBlockPeriodMS,
                        int localBlockPeriodMS,
                        int serverAddress) {
        try {
            long[] clusterAddresses = {serverAddress};
            this.gateway = VanillaGateway.newGateway(serverAddress, "gb1dn", clusterAddresses, mainBlockPeriodMS, localBlockPeriodMS, TransactionBlockEvent._32_MB);
            this.server = new XCLServer("one", serverAddress, serverAddress, secretKey, gateway);
            gateway.start();

        } catch (Throwable t) {
            t.printStackTrace();

        }
    }

    @Override
    public void close() {
        // todo: quietly terminate the BlcokEngine voting thread
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


    // Not using JUnit at the moment because
    // on Windows, using JUnit and the native encryption library will crash the JVM.
    public static void main(String[] args) {
        Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
        Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);

        GuiXclServer guiXclServer = null;
        try {
            guiXclServer = new GuiXclServer(secretKey,1000, 10, 10001);


            System.out.println("before transfer");
            TransferValueCommand tvc1 = new TransferValueCommand(1, 0, 2, 1e-9, "USD", "");
            guiXclServer.transferValueCommand(tvc1);
            System.out.println("after transfer");

            ((VanillaGateway) guiXclServer.gateway).printBalances();

        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
            System.exit(0);
        }

    }


}
