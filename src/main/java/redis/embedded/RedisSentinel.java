package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.common.CommonConstant;

import java.util.List;

@Slf4j
public class RedisSentinel extends AbstractRedisServerInstance {
    private static final String REDIS_SENTINEL_READY_PATTERN = ".*Sentinel (runid|ID) is.*";

    RedisSentinel(List<String> args, Integer sentinelPort) {
        super(args, sentinelPort, CommonConstant.DEFAULT_REDIS_MASTER_PORT);
    }

    public static RedisSentinelBuilder builder() {
        return new RedisSentinelBuilder();
    }

    @Override
    protected String redisServerReadyPattern() {
        return REDIS_SENTINEL_READY_PATTERN;
    }
}
