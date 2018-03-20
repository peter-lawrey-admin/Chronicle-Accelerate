package cash.xcl.api;

import cash.xcl.util.XCLIntObjMap;
import net.openhft.chronicle.bytes.Bytes;

import java.util.Map;
import java.util.function.Supplier;

public class MultiDtoParser implements DtoParser {
    final XCLIntObjMap<DtoParser> dtoParserMap;

    public MultiDtoParser(Map<Integer, Supplier<DtoParser<?>>> protocolDtoParserSupplierMap) {
        dtoParserMap = XCLIntObjMap.withExpectedSize(DtoParser.class, protocolDtoParserSupplierMap.size());
        for (Map.Entry<Integer, Supplier<DtoParser<?>>> entry : protocolDtoParserSupplierMap.entrySet()) {
            dtoParserMap.put(entry.getKey(), entry.getValue().get());
        }
    }

    @Override
    public void parseOne(Bytes bytes, Object messages) {
        int protocol = DtoParser.protocol(bytes);
        DtoParser dtoParser = dtoParserMap.get(protocol);
        if (dtoParser == null)
            throw new IllegalArgumentException("Unsupported protocol " + protocol);
        //noinspection unchecked
        dtoParser.parseOne(bytes, messages);
    }
}
