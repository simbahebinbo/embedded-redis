package redis.embedded.exceptions;

public class OsDetectionException extends RuntimeException {
    public OsDetectionException(String message) {
        super(message);
    }

    public OsDetectionException(Throwable cause) {
        super(cause);
    }

    public OsDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
