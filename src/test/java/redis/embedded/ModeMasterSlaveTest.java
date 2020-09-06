package redis.embedded;

import javax.annotation.concurrent.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

// 主从模式
@Slf4j
@NotThreadSafe
public class ModeMasterSlaveTest extends BaseTest {

  private RedisServer slaveServer;
  private RedisServer masterServer;
  private int masterPort;
  private String masterHost;
  private int slavePort;
  private String slaveHost;

  @BeforeEach
  public void setUp() {
    super.setUp();
    masterHost = CommonConstant.DEFAULT_REDIS_HOST;
    masterPort = RandomUtils.nextInt(10000, 20000);
    slaveHost = CommonConstant.DEFAULT_REDIS_HOST;
    slavePort = RandomUtils.nextInt(10000, 20000);
  }

  // 主从模式
  // 正常启动
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperate() {
    masterServer = RedisServer.builder().port(masterPort).build();
    masterServer.start();

    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机
  // 主节点不可读不可写 从节点可读不可写
  @Test
  public void testOperateThenMasterDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newMasterJedis = masterPool.getResource();
        });

    // 重新获取连接
    Jedis newSlaveJedis = slavePool.getResource();
    writeFail(newSlaveJedis);
    readNothing(newSlaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机，然后重启
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperateThenMasterDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(1000);

    // 重新获取连接
    Jedis newMasterJedis = masterPool.getResource();
    Jedis newSlaveJedis = slavePool.getResource();

    writeSuccess(newMasterJedis);
    readSuccess(newMasterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(newSlaveJedis);
    readNothing(newSlaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 从节点宕机
  // 主节点可读可写 从节点不可读不可写
  @Test
  public void testOperateThenSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);

    // 重新获取连接
    Jedis newMasterJedis = masterPool.getResource();

    writeSuccess(newMasterJedis);
    readSuccess(newMasterJedis);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newSlaveJedis = slavePool.getResource();
        });

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 从节点宕机，然后重启
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperateThenSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(1000);

    // 重新获取连接
    Jedis newMasterJedis = masterPool.getResource();
    Jedis newSlaveJedis = slavePool.getResource();

    writeSuccess(newMasterJedis);
    readSuccess(newMasterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(newSlaveJedis);
    readNothing(newSlaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机  从节点宕机
  // 主节点不可读不可写 从节点不可读不可写
  @Test
  public void testOperateThenMasterDownAndSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newMasterJedis = masterPool.getResource();
        });

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newSlaveJedis = slavePool.getResource();
        });

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机，然后重启；从节点宕机，然后重启
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperateThenMasterDownUpAndSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(1000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(1000);

    // 重新获取连接
    Jedis newMasterJedis = masterPool.getResource();
    Jedis newSlaveJedis = slavePool.getResource();

    writeSuccess(newMasterJedis);
    readSuccess(newMasterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(newSlaveJedis);
    readNothing(newSlaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机；从节点宕机，然后重启
  // 主节点不可读不可写 从节点可读不可写
  @Test
  public void testOperateThenMasterDownAndSlaveDownUp() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);
    // 从节点重启
    slaveServer.start();
    TimeTool.sleep(1000);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newMasterJedis = masterPool.getResource();
        });

    // 重新获取连接
    Jedis newSlaveJedis = slavePool.getResource();
    writeFail(newSlaveJedis);
    readNothing(newSlaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  // 主从模式
  // 正常启动
  // 主节点宕机，然后重启；从节点宕机
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperateThenMasterDownUpAndSlaveDown() {
    masterServer = RedisServer.builder().port(masterPort).build();
    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();

    masterServer.start();
    slaveServer.start();

    JedisPool masterPool = new JedisPool(masterHost, masterPort);
    JedisPool slavePool = new JedisPool(slaveHost, slavePort);
    Jedis masterJedis = masterPool.getResource();
    Jedis slaveJedis = slavePool.getResource();

    writeSuccess(masterJedis);
    readSuccess(masterJedis);
    // 等待主从同步
    TimeTool.sleep(1000);
    writeFail(slaveJedis);
    readNothing(slaveJedis);

    // 主节点宕机
    masterServer.stop();
    TimeTool.sleep(1000);
    // 从节点宕机
    slaveServer.stop();
    TimeTool.sleep(1000);
    // 主节点重启
    masterServer.start();
    TimeTool.sleep(1000);

    // 重新获取连接
    Jedis newMasterJedis = masterPool.getResource();
    writeSuccess(newMasterJedis);
    readSuccess(newMasterJedis);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          // 重新获取连接
          Jedis newSlaveJedis = slavePool.getResource();
        });

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }
}
