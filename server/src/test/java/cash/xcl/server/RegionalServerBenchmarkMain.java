package cash.xcl.server;

import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;

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
    static RegionalServerBenchmarkMain benchmarkMain = null;

    public RegionalServerBenchmarkMain(int mainBlockPeriodMS, int localBlockPeriodMS) throws IOException {
        Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
        long[] clusterAddresses = {serverAddress};
        this.gateway = VanillaGateway.newGateway(serverAddress, "gb1dn", clusterAddresses, mainBlockPeriodMS, localBlockPeriodMS);
        this.server = new XCLServer("one", serverAddress, serverAddress, publicKey, secretKey, gateway).internal(INTERNAL);
        gateway.start();
    }

    public static void main(String[] args) throws IOException {
        benchmarkMain = new RegionalServerBenchmarkMain(1000, 5);
        System.out.println("Server started");
    }

    public void close() {
        Closeable.closeQuietly(server);
    }
}
