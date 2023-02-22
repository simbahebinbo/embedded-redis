package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.common.CommonConstant;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisSentinel extends AbstractRedisServerInstance {
    private static final String REDIS_SENTINEL_READY_PATTERN = ".*Sentinel (runid|ID) is.*";

    public RedisSentinel() {
        this(CommonConstant.DEFAULT_REDIS_SENTINEL_PORT, CommonConstant.DEFAULT_REDIS_MASTER_PORT);
        log.debug("args: " + this.args);
    }

    public RedisSentinel(int sentinelPort, int masterPort) {
        super(sentinelPort, masterPort);
        this.args = builder().sentinelPort(sentinelPort).masterPort(masterPort).build().args;
        log.debug("args: " + this.args);
    }

    public RedisSentinel(List<String> args, int port) {
        super(port, CommonConstant.DEFAULT_REDIS_MASTER_PORT);
        this.args = new ArrayList<>(args);
        log.debug("args: " + this.args);
    }

    public static RedisSentinelBuilder builder() {
        return new RedisSentinelBuilder();
    }

    @Override
    protected String redisReadyPattern() {
        return REDIS_SENTINEL_READY_PATTERN;
    }
}
