package QRAB.QRAB.login.excepiton;

public class NotFoundContentException extends RuntimeException {
    public NotFoundContentException() {
        super();
    }
    public NotFoundContentException(String message, Throwable cause) {
        super(message, cause);
    }
    public NotFoundContentException(String message) {
        super(message);
    }
    public NotFoundContentException(Throwable cause) {
        super(cause);
    }
}