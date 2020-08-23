package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.common.CommonConstant;

// 主从模式
@Slf4j
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

  // 单机模式
  // 正常启动
  // 主节点可读可写 从节点可读不可写
  @Test
  public void testOperate() {
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

//    writeFail(slaveJedis);
//    readSuccess(slaveJedis);

    masterPool.close();
    slavePool.close();
    slaveServer.stop();
    masterServer.stop();
  }

  //  // 单机模式
  //  // 正常启动
  //  // 从节点可读不可写
  //  @Test
  //  public void testOperateSlave() {
  //    masterServer = RedisServer.builder().port(masterPort).build();
  //    slaveServer = RedisServer.builder().port(slavePort).slaveOf(masterHost, masterPort).build();
  //
  //    masterServer.start();
  //    slaveServer.start();
  //
  //    JedisPool pool = new JedisPool(masterHost, masterPort);
  //    Jedis jedis = pool.getResource();
  //    writeSuccess(jedis);
  //    readSuccess(jedis);
  //
  //    pool.close();
  //    slaveServer.stop();
  //    masterServer.stop();
  //  }
}
