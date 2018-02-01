package cash.xcl.api.util;

import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.wire.WireIn;
import net.openhft.chronicle.wire.WireOut;

import java.util.Base64;

public class AddressServiceHandlers {

    private final AddressService addressService;

    public AddressServiceHandlers(AddressService addressService) {
        this.addressService = addressService;
    }

    public void newAddress(WireIn wireIn, WireOut wireOut) {
        String regionCode = wireIn.read("regionCode").text();
        String base64Pk = wireIn.read("publicKey").text();
        byte[] rawPublicKey = Base64.getDecoder().decode(base64Pk);
        BytesStore<?, ?> publicKey = BytesStore.wrap(rawPublicKey);
        long newAddress = addressService.createAddress(regionCode, publicKey);
        wireOut.write("address").text(AddressUtil.encode(newAddress));
    }

    public void getAddress(WireIn wireIn, WireOut wireOut) {
        // String address = wireIn.read("address").text();
        // BytesStore<?, ?> binPublicKey = addressService.getPublicKey(address);
        // wireOut.write("publicKey").text(Base64.getEncoder().encodeToString(binPublicKey.toByteArray()));
    }

}
