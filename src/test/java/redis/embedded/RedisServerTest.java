package redis.embedded;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;
import redis.embedded.util.TimeTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisServerTest {

    private RedisServer redisServer;
    private int port;
    private String host;

    @BeforeEach
    public void setUp() {
        host = CommonConstant.DEFAULT_REDIS_HOST;
        port = RandomUtils.secure().randomInt(10001, 11000);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        TimeTool.sleep(1000L);
        redisServer.stop();
    }

    @Test
    public void shouldNotAllowMultipleRunsWithoutStop() {

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    redisServer = RedisServer.builder().port(port).build();
                    redisServer.start();
                    redisServer.start();
                    redisServer.stop();
                });
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();

        redisServer.start();
        redisServer.stop();
    }

    @Test
    public void shouldIndicateInactiveBeforeStart() {
        redisServer = RedisServer.builder().port(port).build();
        Assertions.assertFalse(redisServer.isActive());
    }

    @Test
    public void testPorts() {
        redisServer = RedisServer.builder().port(port).build();
        Assertions.assertEquals(redisServer.ports(), Set.of(port));
    }

    @Test
    public void shouldIndicateActiveAfterStart() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        Assertions.assertTrue(redisServer.isActive());
        redisServer.stop();
    }

    @Test
    public void shouldIndicateInactiveAfterStop() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        redisServer.stop();
        Assertions.assertFalse(redisServer.isActive());
    }

    @Test
    public void shouldOverrideDefaultExecutable() {
        RedisExecProvider customProvider =
                RedisServerExecProvider.defaultProvider()

                        .override(
                                OS.UNIX,
                                Architecture.AMD64,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_AMD64).getFile())
                        .override(
                                OS.UNIX,
                                Architecture.ARM64,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_ARM64).getFile())
                        .override(
                                OS.MAC_OSX,
                                Architecture.AMD64,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_MAC_OSX_AMD64).getFile())
                        .override(
                                OS.MAC_OSX,
                                Architecture.ARM64,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_MAC_OSX_ARM64).getFile());

        redisServer = new RedisServerBuilder().redisExecProvider(customProvider).port(port).build();
    }

    @Test
    public void shouldFailWhenBadExecutableGiven() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    RedisExecProvider buggyProvider =
                            RedisServerExecProvider.defaultProvider()
                                    .override(OS.UNIX, "some")
                                    .override(OS.MAC_OSX, "some");

                    redisServer =
                            new RedisServerBuilder().redisExecProvider(buggyProvider).port(port).build();
                });
    }

    @Test
    public void testAwaitRedisServerReady() {

        try {
            String readyPattern = RedisServer.builder().build().redisInstanceReadyPattern();

            assertReadyPattern(
                    new BufferedReader(
                            new InputStreamReader(
                                    Objects.requireNonNull(getClass()
                                            .getClassLoader()
                                            .getResourceAsStream("redis-7.x-standalone-startup-output.txt")))),
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
