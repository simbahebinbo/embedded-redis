package redis.embedded;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class RedisServer extends AbstractRedisServerInstance {
    private static final String REDIS_SERVER_READY_PATTERN = ".*(R|r)eady to accept connections.*";

    RedisServer(File executable, Integer port) {
        super(Arrays.asList(executable.getAbsolutePath(), "--port", Integer.toString(port)), port);
    }

    RedisServer(RedisServerExecProvider redisExecProvider, Integer port) throws IOException {
        super(Arrays.asList(redisExecProvider.get().getAbsolutePath(), "--port", Integer.toString(port)), port);
    }

    RedisServer(List<String> args, Integer port) {
        super(args, port);
    }

    public static RedisServerBuilder builder() {
        return new RedisServerBuilder();
    }

    @Override
    protected String redisServerReadyPattern() {
        return REDIS_SERVER_READY_PATTERN;
    }
}
