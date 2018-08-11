package im.xcl.platform.util;

import im.xcl.platform.dto.VanillaSignedMessage;
import net.openhft.chronicle.bytes.Bytes;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

class DtoParselet<T> {
    private final Method method;
    private final int protocol;
    private final int midValue;
    private final VanillaSignedMessage vsm;

    public DtoParselet(Method method, int protocol, int midValue) throws Exception {
        this.method = method;
        this.protocol = protocol;
        this.midValue = midValue;
        this.vsm = createVSM(method, protocol, midValue);
    }

    public DtoParselet(DtoParselet parselet) {
        this.method = parselet.method;
        this.protocol = parselet.protocol;
        this.midValue = parselet.midValue;
        try {
            this.vsm = createVSM(method, protocol, midValue);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @NotNull
    private static VanillaSignedMessage createVSM(Method method, int protocol, int midValue) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<?> type = method.getParameterTypes()[0];
        return (VanillaSignedMessage)
                type.getDeclaredConstructor(int.class, int.class)
                        .newInstance(protocol, midValue);
    }

    public void parse(Bytes bytes, T listener) {
        vsm.readMarshallable(bytes);
        try {
            method.invoke(listener, vsm);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "DtoParselet{" +
                "method=" + method +
                ", protocol=" + protocol +
                ", midValue=" + midValue +
                ", vsm=" + vsm +
                '}';
    }
}
