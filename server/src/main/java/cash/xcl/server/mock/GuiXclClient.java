package cash.xcl.server.mock;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.TransferValueCommand;


import cash.xcl.api.dto.*;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.util.AbstractAllMessages;
import net.openhft.chronicle.bytes.Bytes;



public class GuiXclClient extends AbstractAllMessages {

    private XCLClient client;

    public GuiXclClient(Bytes secretKey,
                        AllMessages allMessages,
                        int serverAddress,
                        long sourceAddress) {
        super(99999999999L);
        try {
            client = new XCLClient("client", "localhost", serverAddress, sourceAddress,
                    secretKey,
                    allMessages);
        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
        }
    }


    @Override
    public void createNewAddressCommand(CreateNewAddressCommand createNewAddressCommand) {
//        this.client.createNewAddressEvent(
//                new CreateNewAddressEvent(0, 0, 0, 0, createNewAddressCommand.sourceAddress(), createNewAddressCommand.publicKey()));

        this.client.createNewAddressCommand(createNewAddressCommand);
    }

    @Override
    public void openingBalanceEvent(OpeningBalanceEvent openingBalanceEvent) {
        this.client.openingBalanceEvent(openingBalanceEvent);
    }

    @Override
    public void subscriptionQuery(SubscriptionQuery subscriptionQuery) {
        this.client.subscriptionQuery(subscriptionQuery);
    }

    @Override
    public void transferValueCommand(TransferValueCommand transferValueCommand) {
        this.client.transferValueCommand(transferValueCommand);
    }







    public void initializeAccounts(long sourceAddress,
                                   long destinationAddress,
                                   Bytes publicKey) {
        try {
            // source address
            client.createNewAddressEvent(new CreateNewAddressEvent(sourceAddress,
                    0, 0, 0,
                    sourceAddress, publicKey));
            final OpeningBalanceEvent obe2 = new OpeningBalanceEvent(
                    sourceAddress,
                    1,
                    sourceAddress,
                    "USD",
                    1000);
            client.openingBalanceEvent(obe2);


            final OpeningBalanceEvent obe1 = new OpeningBalanceEvent(
                    sourceAddress,
                    1,
                    destinationAddress,
                    "USD",
                    1000);
            client.openingBalanceEvent(obe1);

        } catch (Throwable t) {
            t.printStackTrace();

        } finally {
            //Jvm.pause(1000);
            //benchmarkMain.close();
        }
    }


    public void transfer(TransferValueCommand tvc1, Bytes publicKey) {
        initializeAccounts( tvc1.sourceAddress(), tvc1.toAddress(), publicKey);
        client.transferValueCommand(tvc1);
    }
}
