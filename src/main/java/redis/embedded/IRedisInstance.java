package redis.embedded;

import redis.embedded.enums.RedisInstanceModeEnum;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.io.IOException;


public interface IRedisInstance {
    boolean isActive();

    void doStart(RedisInstanceModeEnum instanceMode) throws EmbeddedRedisException;

    void installExitHook(String name);

    void logStandardError();

    void awaitRedisInstanceReady() throws IOException;

    ProcessBuilder createRedisProcessBuilder();

    void doStop() throws EmbeddedRedisException;

    void tryWaitFor();
}
