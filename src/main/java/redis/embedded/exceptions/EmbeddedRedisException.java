package redis.embedded.exceptions;

public class EmbeddedRedisException extends RuntimeException {
    public EmbeddedRedisException(String message) {
        super(message);
    }

    public EmbeddedRedisException(Throwable cause) {
        super(cause);
    }

    public EmbeddedRedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
