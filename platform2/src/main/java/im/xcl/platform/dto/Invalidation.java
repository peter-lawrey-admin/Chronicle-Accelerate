package im.xcl.platform.dto;

/**
 * This message states this node verifies a given public key after connecting to it successfully.
 */
public class Invalidation extends SelfSignedMessage<Invalidation> {

    public Invalidation(int protocol, int messageType) {
        super(protocol, messageType);
    }

}
