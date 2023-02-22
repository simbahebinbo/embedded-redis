package redis.embedded;

import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.Set;

public interface IRedisServer {
    boolean isActive();

    void start() throws EmbeddedRedisException;

    void stop() throws EmbeddedRedisException;

    Set<Integer> ports();
}
