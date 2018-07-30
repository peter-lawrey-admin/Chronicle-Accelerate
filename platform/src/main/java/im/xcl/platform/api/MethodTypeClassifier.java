package im.xcl.platform.api;

/**
 * Classify each method type to check messages are passed to the right place.
 */
public interface MethodTypeClassifier {
    /**
     * Classify a methodId
     *
     * @param methodId to classify
     * @return the MethodIdType
     */
    MethodIdType classify(int methodId);
}
