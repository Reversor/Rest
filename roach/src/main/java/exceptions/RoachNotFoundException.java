package exceptions;

public class RoachNotFoundException extends Exception {

    public RoachNotFoundException() {
        super();
    }

    public RoachNotFoundException(String message) {
        super(message);
    }
}
