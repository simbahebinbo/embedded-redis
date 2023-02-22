package redis.embedded;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RedisClient extends AbstractRedisClientInstance {
    private static final String REDIS_CLIENT_READY_PATTERN = ".*All 16384 slots covered.*";

    RedisClient(List<String> args) {
        super(args);
    }

    public static RedisClientBuilder builder() {
        return new RedisClientBuilder();
    }

    @Override
    protected String redisClientReadyPattern() {
        return REDIS_CLIENT_READY_PATTERN;
    }
}
