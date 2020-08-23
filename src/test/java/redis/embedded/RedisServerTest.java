package redis.embedded;

import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;
import redis.embedded.util.TimeUtil;

@Slf4j
public class RedisServerTest {

  private RedisServer redisServer;
  private int port;
  private String host;

  @BeforeEach
  public void setUp() {
    host = CommonConstant.DEFAULT_REDIS_HOST;
    port = RandomUtils.nextInt(10000, 20000);
  }

  @Test
  @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
  public void testSimpleRun() {
    redisServer = new RedisServer(port);
    redisServer.start();
    TimeUtil.sleep(1000L);
    redisServer.stop();
  }

  @Test
  public void shouldNotAllowMultipleRunsWithoutStop() {

    Assertions.assertThrows(
        Exception.class,
        () -> {
          redisServer = new RedisServer(port);
          redisServer.start();
          redisServer.start();
          redisServer.stop();
        });
  }

  @Test
  public void shouldAllowSubsequentRuns() {
    redisServer = new RedisServer(port);
    redisServer.start();
    redisServer.stop();

    redisServer.start();
    redisServer.stop();

    redisServer.start();
    redisServer.stop();
  }

  @Test
  public void shouldIndicateInactiveBeforeStart() {
    redisServer = new RedisServer(port);
    Assertions.assertFalse(redisServer.isActive());
  }

  @Test
  public void shouldIndicateActiveAfterStart() {
    redisServer = new RedisServer(port);
    redisServer.start();
    Assertions.assertTrue(redisServer.isActive());
    redisServer.stop();
  }

  @Test
  public void shouldIndicateInactiveAfterStop() {
    redisServer = new RedisServer(port);
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
                Architecture.X86,
                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_X86).getFile())
            .override(
                OS.UNIX,
                Architecture.AMD64,
                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_UNIX_AMD64).getFile())
            .override(
                OS.MAC_OSX,
                Resources.getResource(CommonConstant.REDIS_SERVER_EXEC_MAC_OSX).getFile());

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

      String readyPattern = RedisServer.builder().build().redisReadyPattern();

      assertReadyPattern(
          new BufferedReader(
              new InputStreamReader(
                  getClass()
                      .getClassLoader()
                      .getResourceAsStream("redis-6.x-standalone-startup-output.txt"))),
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
