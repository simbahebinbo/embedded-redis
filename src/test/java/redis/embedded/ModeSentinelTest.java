package redis.embedded;

import com.google.common.collect.Sets;
import javax.annotation.concurrent.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

// 哨兵模式
@Slf4j
@NotThreadSafe
public class ModeSentinelTest extends BaseTest {

  private RedisSentinel sentinelServer;
  private RedisServer masterServer;
  private RedisServer slaveServer;
  private int masterPort;
  private String masterHost;
  private int slavePort;
  private String slaveHost;
  private int sentinelPort;
  private String sentinelHost;
  private String masterName;

  @BeforeEach
  public void setUp() {
    super.setUp();
    masterHost = CommonConstant.DEFAULT_REDIS_HOST;
    masterPort = RandomUtils.nextInt(10000, 60000);
    slaveHost = CommonConstant.DEFAULT_REDIS_HOST;
    slavePort = RandomUtils.nextInt(10000, 60000);
    sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
    sentinelPort = RandomUtils.nextInt(10000, 60000);
    masterName = RandomStringUtils.randomAlphabetic(5, 10);
  }

  // 哨兵模式
  // 正常启动
  // 哨兵节点可读可写
  @Test
  public void testOperate() {
    masterServer = RedisServer.builder().port(masterPort).build();
    masterServer.start();

    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();
    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    sentinelPool.close();
    sentinelServer.stop();
    masterServer.stop();
    slaveServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机
  // 哨兵节点可读可写
  @Test
  public void testOperateThenMasterDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    // 等待故障转移
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机，然后重启
  // 哨兵节点可读可写
  @Test
  public void testOperateThenMasterDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    // 等待故障转移
    TimeTool.sleep(5000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 从节点宕机
  // 哨兵节点可读可写
  @Test
  public void testOperateThenSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 从节点宕机，然后重启
  // 哨兵节点可读可写
  @Test
  public void testOperateThenSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机  从节点宕机
  // 哨兵节点不可读不可写
  @Test
  public void testOperateThenMasterDownAndSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(5000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newMasterJedis = sentinelPool.getResource();
        });

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机，然后重启；从节点宕机，然后重启
  // 哨兵节点可读可写
  @Test
  public void testOperateThenMasterDownUpAndSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(5000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(5000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机；从节点宕机，然后重启
  // 哨兵节点可读不可写
  @Test
  public void testOperateThenMasterDownAndSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(5000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeFail(newSentinelJedis);
    readNothing(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 主节点宕机，然后重启；从节点宕机
  // 哨兵节点可读可写
  @Test
  public void testOperateThenMasterDownUpAndSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(5000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(5000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 哨兵节点宕机
  // 哨兵节点可读可写
  @Test
  public void testOperateThenSentinelDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 哨兵节点宕机
    sentinelServer.stop();
    TimeTool.sleep(5000);

    // 重新获取连接
    Jedis newSentinelJedis = sentinelPool.getResource();
    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }

  // 哨兵模式
  // 正常启动
  // 哨兵节点宕机，然后重启
  // 哨兵节点可读可写
  @Test
  public void testOperateThenSentinelDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    sentinelServer =
        RedisSentinel.builder()
            .sentinelPort(sentinelPort)
            .masterPort(masterPort)
            .masterName(masterName)
            .downAfterMilliseconds(1000L)
            .failoverTimeout(1000L)
            .quorumSize(1)
            .build();
    sentinelServer.start();

    JedisSentinelPool sentinelPool =
        new JedisSentinelPool(
            masterName,
            Sets.newHashSet(sentinelHost + CommonConstant.SEPARATOR_COLON + sentinelPort));
    Jedis sentinelJedis = sentinelPool.getResource();

    writeSuccess(sentinelJedis);
    readSuccess(sentinelJedis);

    // 哨兵节点宕机
    sentinelServer.stop();
    TimeTool.sleep(5000);
    // 哨兵节点重启
    sentinelServer.start();
    TimeTool.sleep(5000);

    // 重新获取链接
    Jedis newSentinelJedis = sentinelPool.getResource();

    writeSuccess(newSentinelJedis);
    readSuccess(newSentinelJedis);

    sentinelPool.close();
    slaveServer.stop();
    masterServer.stop();
    sentinelServer.stop();
  }
}
