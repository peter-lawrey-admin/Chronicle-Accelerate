package im.xcl.platform.api;

public class VanillaMethodTypeClassifier implements MethodTypeClassifier {
    /**
     * This provides the default classifications for different MethodIds.
     * This class can be overridden to customise esp the small MethodIds.
     *
     * @param methodId to classify
     * @return the MethodIdType
     */
    @Override
    public MethodIdType classify(int methodId) {
        if (methodId < ~0x4000)
            return MethodIdType.INVALID;
        if (methodId < 0)
            return MethodIdType.INTERNAL;
        if (methodId < 0x80)
            return MethodIdType.INVALID;
        if (methodId < 0x1000)
            return MethodIdType.TRANSACTION_COMMAND;
        if (methodId < 0x2000)
            return MethodIdType.TRANSACTION_RESULT;
        if (methodId < 0x3000)
            return MethodIdType.QUERY_REQUEST;
        if (methodId < 0x4000)
            return MethodIdType.QUERY_RESPONSE;
        return MethodIdType.INVALID;
    }
}
