package im.xcl.platform.util;

import cash.xcl.util.XCLIntObjMap;
import net.openhft.chronicle.bytes.MethodId;
import net.openhft.chronicle.core.Maths;

import java.lang.reflect.Method;
import java.util.function.Supplier;

public class DtoParserBuilder<T> implements Supplier<DtoParser<T>> {

    private final XCLIntObjMap<DtoParselet> parseletMap = XCLIntObjMap.withExpectedSize(DtoParselet.class, 128);

    public DtoParserBuilder<T> addProtocol(int protocol, Class<? super T> pClass) {
        for (Method method : pClass.getDeclaredMethods()) {
            MethodId mid = method.getAnnotation(MethodId.class);
            if (mid != null) {
                int key = protocol << 16 + (mid.value() & 0xFFFF);
                try {
                    parseletMap.put(key,
                            new DtoParselet(method, protocol, Maths.toUInt16(mid.value())));
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return this;
    }

    @Override
    public DtoParser<T> get() {
        XCLIntObjMap<DtoParselet> parseletMap2 = XCLIntObjMap.withExpectedSize(DtoParselet.class, parseletMap.size() * 2);
        parseletMap.forEach((i, dp) -> parseletMap2.put(i, new DtoParselet(dp)));
        return new VanillaDtoParser<T>(parseletMap2);
    }

}
