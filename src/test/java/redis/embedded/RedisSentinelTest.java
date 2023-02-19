package redis.embedded;

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
import redis.embedded.util.TimeTool;

@Slf4j
public class RedisSentinelTest {
  private RedisSentinel sentinelServer;
  private RedisServer masterServer;
  private int masterPort;
  private String masterHost;
  private int sentinelPort;
  private String sentinelHost;

  @BeforeEach
  public void setUp() {
    masterHost = CommonConstant.DEFAULT_REDIS_HOST;
    masterPort = RandomUtils.nextInt(10000, 60000);
    sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
    sentinelPort = RandomUtils.nextInt(10000, 60000);
  }

  @Test
  @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
  public void testSimpleRun() {
    sentinelServer = new RedisSentinel(sentinelPort, masterPort);
    sentinelServer.start();
    TimeTool.sleep(1000L);
    sentinelServer.stop();
  }

  @Test
  public void shouldAllowSubsequentRuns() {
    sentinelServer =
        RedisSentinel.builder().sentinelPort(sentinelPort).masterPort(masterPort).build();
    sentinelServer.start();
    sentinelServer.stop();

    sentinelServer.start();
    sentinelServer.stop();

    sentinelServer.start();
    sentinelServer.stop();
  }

  @Test
  public void testAwaitRedisSentinelReady() {
    try {
      String readyPattern =
          RedisSentinel.builder()
              .sentinelPort(sentinelPort)
              .masterPort(masterPort)
              .build()
              .redisReadyPattern();

      assertReadyPattern(
          new BufferedReader(
              new InputStreamReader(
                  getClass()
                      .getClassLoader()
                      .getResourceAsStream("redis-7.x-sentinel-startup-output.txt"))),
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
