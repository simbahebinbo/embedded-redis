package redis.embedded;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class RedisClientTest {

    private RedisClient redisClient;
    private int port;
    private String host;

    @BeforeEach
    public void setUp() {
        host = CommonConstant.DEFAULT_REDIS_HOST;
        port = RandomUtils.nextInt(10001, 11000);
    }

    @Test
    public void testMatch() {
        String pattern = ".*All 16384 slots covered.*";
        String content = "[OK] All 16384 slots covered.";
        Assertions.assertTrue(Pattern.matches(pattern, content));
    }

    @Test
    public void shouldIndicateInactiveBeforeStart() {
        redisClient = new RedisClient(Lists.newArrayList());
        Assertions.assertFalse(redisClient.isActive());
    }

    @Test
    public void shouldOverrideDefaultExecutable() {
        RedisExecProvider customProvider =
                RedisCliExecProvider.defaultProvider()
                        .override(
                                OS.UNIX,
                                Architecture.X86,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_X86).getFile())
                        .override(
                                OS.UNIX,
                                Architecture.AMD64,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_AMD64).getFile())
                        .override(
                                OS.MAC_OSX,
                                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_MAC_OSX).getFile());

        redisClient = new RedisClientBuilder().redisExecProvider(customProvider).ports(Collections.singletonList(port)).build();
    }

    @Test
    public void shouldFailWhenBadExecutableGiven() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    RedisExecProvider buggyProvider =
                            RedisCliExecProvider.defaultProvider()
                                    .override(OS.UNIX, "some")
                                    .override(OS.MAC_OSX, "some");

                    redisClient =
                            new RedisClientBuilder().redisExecProvider(buggyProvider).ports(Collections.singletonList(port)).build();
                });
    }

    @Test
    public void testAwaitRedisClientReady() {

        try {
            String readyPattern = RedisClient.builder().build().redisReadyPattern();

            assertReadyPattern(
                    new BufferedReader(
                            new InputStreamReader(
                                    Objects.requireNonNull(getClass()
                                            .getClassLoader()
                                            .getResourceAsStream("redis-7.x-client-run-output.txt")))),
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
