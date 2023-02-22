package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.List;

@Slf4j
abstract class AbstractRedisClientInstance extends AbstractRedisInstance implements IRedisClient {
    AbstractRedisClientInstance(List<String> args) {
        super(args);
    }

    public synchronized void run() throws EmbeddedRedisException {
        doStart();
    }

    @Override
    public String redisInstanceReadyPattern() {
        return redisClientReadyPattern();
    }

    protected abstract String redisClientReadyPattern();
}
