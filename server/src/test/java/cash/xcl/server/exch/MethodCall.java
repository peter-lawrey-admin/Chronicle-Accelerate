package cash.xcl.server.exch;

public class MethodCall {
    private final String methodName;
    private final Object[] params;

    public MethodCall(String methodName, Object[] params) {
        super();
        this.methodName = methodName;
        this.params = params;
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
