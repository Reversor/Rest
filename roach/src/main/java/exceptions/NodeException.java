package exceptions;

public class NodeException extends Exception {

    public NodeException() {
        super();
    }

    public NodeException(String message) {
        super(message);
    }

    public NodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
