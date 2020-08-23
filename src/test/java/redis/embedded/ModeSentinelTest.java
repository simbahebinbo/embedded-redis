package redis.embedded;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.embedded.common.CommonConstant;

@Slf4j
public class ModeSentinelTest extends BaseTest {

  private RedisSentinel sentinelServer;
  private RedisServer masterServer;
  private int masterPort;
  private String masterHost;
  private int sentinelPort;
  private String sentinelHost;
  private String masterName;

  @BeforeEach
  public void setUp() {
    super.setUp();
    masterHost = CommonConstant.DEFAULT_REDIS_HOST;
    masterPort = RandomUtils.nextInt(10000, 20000);
    sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
    sentinelPort = RandomUtils.nextInt(10000, 20000);
    masterName = RandomStringUtils.randomAlphabetic(5, 10);
  }

  @Test
  public void testSimpleOperationsAfterRun() {
    masterServer = RedisServer.builder().port(masterPort).build();
    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .build();
    masterServer.start();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis jedis = sentinelPool.getResource();
    writeSuccess(jedis);
    readSuccess(jedis);

    sentinelPool.close();
    sentinelServer.stop();
    masterServer.stop();
  }
}
