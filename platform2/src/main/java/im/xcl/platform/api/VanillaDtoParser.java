package im.xcl.platform.api;

import cash.xcl.util.XCLIntObjMap;
import net.openhft.chronicle.bytes.Bytes;

public class VanillaDtoParser<T> implements DtoParser<T> {
    private final XCLIntObjMap<DtoParselet> parseletMap;

    public VanillaDtoParser(XCLIntObjMap<DtoParselet> parseletMap) {
        this.parseletMap = parseletMap;
    }

    @Override
    public void parseOne(Bytes bytes, T listener) {
        int protocolMessageType = bytes.readInt(bytes.readPosition() + VanillaSignedMessage.MESSAGE_TYPE);
        DtoParselet parselet = parseletMap.get(protocolMessageType);
        parselet.parse(bytes, listener);
    }
}
