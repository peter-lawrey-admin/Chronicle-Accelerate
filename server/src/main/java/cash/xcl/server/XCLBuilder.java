package cash.xcl.server;

import cash.xcl.api.AllMessages;
import cash.xcl.api.dto.CreateNewAddressEvent;
import cash.xcl.api.tcp.XCLClient;
import cash.xcl.api.tcp.XCLServer;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.salt.Ed25519;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class XCLBuilder {
    private Bytes privateKey = Bytes.allocateDirect(Ed25519.PRIVATE_KEY_LENGTH);
    private Bytes publicKey = Bytes.allocateDirect(Ed25519.PUBLIC_KEY_LENGTH);
    private Bytes secretKey = Bytes.allocateDirect(Ed25519.SECRET_KEY_LENGTH);
    private Set<Long> clusterAddresses = new LinkedHashSet<>();
    private long serverAddress = 0;
    private int mainBlockPeriodMS = 1000;
    private int localBlockPeriodMS = 100;
    private String region = "test";
    private boolean internal = false;

    public XCLServer createServer(int port) throws IOException {
        return createServer("server:" + port, port);
    }

    public XCLServer createServer(String name, int port) throws IOException {
        long serverAddress = this.serverAddress;
        if (serverAddress == 0)
            serverAddress = port;

        if (publicKey.isEmpty() || secretKey.isEmpty()) {
            if (privateKey.isEmpty())
                Ed25519.generatePublicAndSecretKey(publicKey, secretKey);
            else
                Ed25519.privateToPublicAndSecret(publicKey, secretKey, privateKey);
        }

        boolean addressAdded = clusterAddresses.add(serverAddress);
        long[] clusterAddressArray =
                clusterAddresses.stream()
                        .mapToLong(i -> i)
                        .toArray();

        VanillaGateway gateway = VanillaGateway.newGateway(serverAddress, region, clusterAddressArray,
                mainBlockPeriodMS, localBlockPeriodMS
        );
        XCLServer server = new XCLServer(name, port, serverAddress, publicKey, secretKey, gateway)
                .internal(internal);
        gateway.start();
        // register the address - otherwise, verify will fail
        gateway.createNewAddressEvent(
                new CreateNewAddressEvent(serverAddress, 0, 0, 0, serverAddress, publicKey));

        if (addressAdded)
            clusterAddresses.remove(serverAddress);
        return server;
    }

    public XCLClient createClient(String name, String hostname, int port, long serverAddress, AllMessages allMessages) {
        return new XCLClient(name, hostname, port, serverAddress, secretKey, allMessages)
                .internal(internal);
    }

    public XCLBuilder serverAddress(long serverAddress) {
        this.serverAddress = serverAddress;
        clusterAddresses.add(serverAddress);
        return this;
    }

    public XCLBuilder addClusterAddress(long serverAddress) {
        clusterAddresses.add(serverAddress);
        return this;
    }

    public Bytes publicKey() {
        return publicKey;
    }

    public XCLBuilder publicKey(Bytes publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public Bytes secretKey() {
        return secretKey;
    }

    public XCLBuilder secretKey(Bytes secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public Set<Long> clusterAddresses() {
        return clusterAddresses;
    }

    public XCLBuilder clusterAddresses(Set<Long> clusterAddresses) {
        this.clusterAddresses = clusterAddresses;
        return this;
    }

    public long serverAddress() {
        return serverAddress;
    }

    public int mainBlockPeriodMS() {
        return mainBlockPeriodMS;
    }

    public XCLBuilder mainBlockPeriodMS(int mainBlockPeriodMS) {
        this.mainBlockPeriodMS = mainBlockPeriodMS;
        return this;
    }

    public int localBlockPeriodMS() {
        return localBlockPeriodMS;
    }

    public XCLBuilder localBlockPeriodMS(int localBlockPeriodMS) {
        this.localBlockPeriodMS = localBlockPeriodMS;
        return this;
    }

    public String region() {
        return region;
    }

    public XCLBuilder region(String region) {
        this.region = region;
        return this;
    }

    public boolean internal() {
        return internal;
    }

    public XCLBuilder internal(boolean internal) {
        this.internal = internal;
        return this;
    }
}
