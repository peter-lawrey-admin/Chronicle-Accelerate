package cash.xcl.server.exch;

public class TransactionFailedException extends Exception {

    private static final long serialVersionUID = 571403343093644847L;

    public TransactionFailedException() {
        super();
    }

    public TransactionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionFailedException(String message) {
        super(message);
    }

    public TransactionFailedException(Throwable cause) {
        super(cause);
    }

}
