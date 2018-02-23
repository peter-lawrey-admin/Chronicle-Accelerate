package cash.xcl.server.exch;

import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.Wires;

public class MethodCall {
    private final String methodName;
    private final Object[] params;

    public MethodCall(String methodName, Object[] params) {
        super();
        this.methodName = methodName;
        this.params = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Marshallable) {
                this.params[i] = Wires.deepCopy((Marshallable) params[i]);
            } else {
                this.params[i] = params[i];
            }
        }

    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParams() {
        return params;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParams(int index) {
        return (T) params[index];
    }

}
