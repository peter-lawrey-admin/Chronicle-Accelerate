package im.xcl.platform.api;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.salt.Ed25519;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenerateKeyForIPMain {
    public static void main(String[] args) {
        String[] ips = {
                "217.160.130.141",
                "213.165.69.177",
                "176.35.46.43",
//                "213.165.69.44",
                "212.227.255.222"
        };
        List<InetAddress> inetAddresses = Stream.of(ips)
                .map(GenerateKeyForIPMain::getByName)
                .collect(Collectors.toList());
        Set<Integer> addresses = inetAddresses.stream()
                .map(InetAddress::hashCode)
                .collect(Collectors.toSet());

//        long start  = System.nanoTime();
//        AtomicLong count = new AtomicLong();
        int nThreads = Runtime.getRuntime().availableProcessors();
        IntStream.range(0, nThreads)
                .parallel()
                .forEach(t -> {
                    Bytes secretKey = Ed25519.allocateSecretKey();
                    Bytes publicKey = Ed25519.allocatePublicKey();
                    Bytes privateKey = Ed25519.allocatePrivateKey();
                    int batchSize = 1024;
                    int i = batchSize;
                    Ed25519.generatePrivateKey(privateKey);
                    while (true) {
                        if (--i < 0) {
                            Ed25519.generatePrivateKey(privateKey);
                            i = batchSize;
//                            long c = count.addAndGet(batchSize);
//                            long time = System.nanoTime() - start;
//                            long rate = (long) (1e9 * c / time);
//                            System.out.println(rate + " per second");
                        } else {
                            privateKey.addAndGetLong(0, 0x123456789ABCDEFL);
                        }
                        Ed25519.privateToPublicAndSecret(publicKey.clear(), secretKey.clear(), privateKey);

                        int ip = publicKey.readInt(publicKey.readRemaining() - Integer.BYTES);
//                        System.err.println(secretKey.toHexString());
//                        System.out.println(Integer.toHexString(ip));
                        if (addresses.contains(ip)) {
                            System.out.println(secretKey.toHexString());
                            return;
                        }
                    }
                });
    }

    static InetAddress getByName(String s) {
        try {
            return InetAddress.getByName(s);
        } catch (UnknownHostException e) {
            throw Jvm.rethrow(e);
        }
    }
}
