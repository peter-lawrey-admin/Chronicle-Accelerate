package cash.xcl.server;

import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.dto.CurrentBalanceQuery;
import cash.xcl.api.dto.ExchangeRateQuery;
import cash.xcl.api.dto.TransactionBlockEvent;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static cash.xcl.server.RegionalClientBenchmarkMain.sendOpeningBalance;

/*
-XX:+UnlockCommercialFeatures
-XX:+FlightRecorder
-XX:+UnlockDiagnosticVMOptions
-XX:+DebugNonSafepoints
-XX:StartFlightRecording=name=test,filename=test.jfr,dumponexit=true,settings=profile
-DfastJava8IO=true

Intel(R) Core(TM) i7-7820X CPU @ 3.60GHz, Centos 7 with linux 4.12
TBA

Intel i7-7700 CPU, Windows 10, 32 GB memory.
benchmark - oneThread = 331318 sustained, 416312 burst messages per second
benchmark - twoThreads = 464425 sustained, 755085 burst messages per second
benchmark - fourThreads = 465087 sustained, 994536 burst messages per second
benchmark - eightThreads = 431039 sustained, 808222 burst messages per second

Intel i7-7820X, Windows 10, 64 GB memory.
as a core service
benchmark - oneThread = 372916 sustained, 476293 burst messages per second
benchmark - twoThreads = 441794 sustained, 686253 burst messages per second
benchmark - fourThreads = 421588 sustained, 743256 burst messages per second
benchmark - eightThreads = 393173 sustained, 683544 burst messages per second

with verify/sign
benchmark - oneThread = 9652 sustained, 180129 burst messages per second
benchmark - twoThreads = 18885 sustained, 441968 burst messages per second
benchmark - fourThreads = 32032 sustained, 576003 burst messages per second
benchmark - eightThreads = 52218 sustained, 489249 burst messages per second
*/

public class RegionalServerBenchmarkMain {

    static final boolean INTERNAL = Boolean.getBoolean("internal");
    private XCLServer server;
    private Gateway gateway;
    private int serverAddress = 10001;

    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);


    public RegionalServerBenchmarkMain(int mainBlockPeriodMS,
                                       int localBlockPeriodMS,
                                       int iterations,
                                       int clientThreads) throws IOException {

        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);


        long[] clusterAddresses = {serverAddress};

        this.gateway = VanillaGateway.newGateway(serverAddress, "gb1dn", clusterAddresses,
                mainBlockPeriodMS, localBlockPeriodMS,
                TransactionBlockEvent._2_MB);


        this.server = new XCLServer("one", serverAddress, serverAddress, secretKey, gateway)
                .internal(INTERNAL);
        gateway.start();
        // register the address - otherwise, verify will fail
        gateway.createNewAddressEvent(new CreateNewAddressEvent(serverAddress, 0, 0, 0, serverAddress, publicKey));
        // register all the addresses involved in the transfers
        // -source and destination accounts- in the Account Service with a opening balance of $1,000,000,000
        for (int iterationNumber = 0; iterationNumber < iterations; iterationNumber++) {
            for (int s = 0; s < clientThreads; s++) {
                final int sourceAddress = (iterationNumber * 100) + s + 1;
                final int destinationAddress = sourceAddress + 1000000;
                gateway.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0,
                        sourceAddress, publicKey));

                gateway.createNewAddressEvent(new CreateNewAddressEvent(0, 0, 0, 0,
                        destinationAddress, publicKey));

// TODO
                ExchangeRateQuery err = new ExchangeRateQuery(0, 0, "XCL", "USD");
                gateway.exchangeRateQuery(err);

                CurrentBalanceQuery cbq = new CurrentBalanceQuery(0, 0, 1000);
                gateway.currentBalanceQuery(cbq);

                AtomicInteger count = new AtomicInteger();
                XCLClient client = new XCLClient("client", "localhost", serverAddress, sourceAddress, secretKey,
                        new MyWritingAllMessages(count))
                        .internal(INTERNAL);
                sendOpeningBalance(client, sourceAddress, sourceAddress);
                sendOpeningBalance(client, sourceAddress, destinationAddress);
                // how do we know if the openingBalanceEvent msg was a success or a failure?
            }
        }
    }

    // Not using JUnit at the moment because
    // on Windows, using JUnit and the native encryption library will crash the JVM.
    static RegionalServerBenchmarkMain benchmarkMain = null;

    public static void main(String[] args) throws IOException {
        System.out.println("internal= " + INTERNAL);
        benchmarkMain = new RegionalServerBenchmarkMain(1000, 10, 10, 8);

    }
}
