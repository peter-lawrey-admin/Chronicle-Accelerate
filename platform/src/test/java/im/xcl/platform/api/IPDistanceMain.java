package im.xcl.platform.api;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IPDistanceMain {
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
        int[] addresses = inetAddresses.stream()
                .mapToInt(InetAddress::hashCode)
                .toArray();
        for (int i = 0; i < addresses.length - 1; i++) {
            int ia = addresses[i] >>> 12;
            for (int j = i + 1; j < addresses.length; j++) {
                int ja = addresses[j] >>> 12;
                System.out.println(Integer.toHexString(ia)
                        + " to " + Integer.toHexString(ja)
                        + " distance " + Integer.bitCount(ia ^ ja));
            }
        }
    }
}
