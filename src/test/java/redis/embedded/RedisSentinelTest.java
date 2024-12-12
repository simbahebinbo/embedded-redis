package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisSentinelTest {
    private RedisSentinel redisSentinel;
    private RedisServer masterServer;
    private Integer masterPort;
    private String masterHost;
    private Integer sentinelPort;
    private String sentinelHost;

    @BeforeEach
    public void setUp() {
        masterHost = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort = (Integer) RandomUtils.secure().randomInt(10001, 11000);
        sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
        sentinelPort = (Integer) RandomUtils.secure().randomInt(11001, 12000);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        redisSentinel = RedisSentinel.builder().sentinelPort(sentinelPort).masterPort(masterPort).build();
        redisSentinel.start();
        TimeTool.sleep(1000L);
        redisSentinel.stop();
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        redisSentinel =
                RedisSentinel.builder().sentinelPort(sentinelPort).masterPort(masterPort).build();
        redisSentinel.start();
        redisSentinel.stop();

        redisSentinel.start();
        redisSentinel.stop();

        redisSentinel.start();
        redisSentinel.stop();
    }

    @Test
    public void testAwaitRedisSentinelReady() {
        try {
            redisSentinel =
                    RedisSentinel.builder().sentinelPort(sentinelPort).masterPort(masterPort).build();
            String readyPattern = redisSentinel.redisInstanceReadyPattern();

            assertReadyPattern(
                    new BufferedReader(
                            new InputStreamReader(
                                    Objects.requireNonNull(getClass()
                                            .getClassLoader()
                                            .getResourceAsStream("redis-7.x-sentinel-startup-output.txt")))),
                    readyPattern);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            log.warn(e.getMessage());
            Assertions.fail();
        }
    }

    private void assertReadyPattern(BufferedReader reader, String readyPattern) throws IOException {
        String outputLine;
        do {
            outputLine = reader.readLine();
            Assertions.assertNotNull(outputLine);
        } while (!outputLine.matches(readyPattern));
    }
}
