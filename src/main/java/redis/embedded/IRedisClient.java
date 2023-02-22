package redis.embedded;

import redis.embedded.exceptions.EmbeddedRedisException;

public interface IRedisClient {
    void run() throws EmbeddedRedisException;
}
