package redis.embedded;

import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.List;

public interface IRedisServer {
    boolean isActive();

    void start() throws EmbeddedRedisException;

    void stop() throws EmbeddedRedisException;

    List<Integer> ports();
}
