package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.JedisUtil;
import redis.embedded.util.TimeTool;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Set;

// 哨兵模式
@Slf4j
@NotThreadSafe
public class ModeSentinelTest extends JedisBaseTest {

    private RedisSentinel redisSentinel;
    private RedisServer masterServer;
    private RedisServer slaveServer;
    private Integer masterPort;

    private String masterHost;
    private Integer slavePort;

    private String slaveHost;
    private Integer sentinelPort;

    private String sentinelHost;
    private String masterName;

    @BeforeEach
    public void setUp() {
        super.setUp();
        masterHost = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort = (Integer) RandomUtils.secure().randomInt(10001, 11000);
        slaveHost = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort = (Integer) RandomUtils.secure().randomInt(11001, 12000);
        sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
        sentinelPort = (Integer) RandomUtils.secure().randomInt(12001, 13000);
        masterName = RandomStringUtils.secure().nextAlphabetic(50, 100);
    }

    // 哨兵模式
    // 正常启动
    // 哨兵节点可读可写
    @Test
    public void testOperate() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();
        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisSentinel.stop();
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
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        // 主节点宕机
        masterServer.stop();
        // 等待故障转移
        TimeTool.sleep(10000);

        // 重新获取链接
        Jedis newSentinelJedis = sentinelPool.getResource();

        writeSuccess(newSentinelJedis);
        readSuccess(newSentinelJedis);

        sentinelPool.close();
        slaveServer.stop();
        masterServer.stop();
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 主节点宕机，然后重启
    // 哨兵节点可读可写
    @Test
    public void testOperateThenMasterDownUp() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        // 主节点宕机
        masterServer.stop();
        // 等待故障转移
        TimeTool.sleep(10000);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 从节点宕机
    // 哨兵节点可读可写
    @Test
    public void testOperateThenSlaveDown() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 从节点宕机，然后重启
    // 哨兵节点可读可写
    @Test
    public void testOperateThenSlaveDownUp() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 主节点宕机  从节点宕机
    // 哨兵节点不可读不可写
    @Test
    public void testOperateThenMasterDownAndSlaveDown() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 主节点宕机，然后重启；从节点宕机，然后重启
    // 哨兵节点可读可写
    @Test
    public void testOperateThenMasterDownUpAndSlaveDownUp() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 主节点宕机；从节点宕机，然后重启
    // 哨兵节点可读不可写
    @Test
    public void testOperateThenMasterDownAndSlaveDownUp() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 主节点宕机，然后重启；从节点宕机
    // 哨兵节点可读可写
    @Test
    public void testOperateThenMasterDownUpAndSlaveDown() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
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
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 哨兵节点宕机
    // 哨兵节点可读可写
    @Test
    public void testOperateThenSentinelDown() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        // 哨兵节点宕机
        redisSentinel.stop();
        TimeTool.sleep(5000);

        // 重新获取连接
        Jedis newSentinelJedis = sentinelPool.getResource();
        writeSuccess(newSentinelJedis);
        readSuccess(newSentinelJedis);

        sentinelPool.close();
        slaveServer.stop();
        masterServer.stop();
        redisSentinel.stop();
    }

    // 哨兵模式
    // 正常启动
    // 哨兵节点宕机，然后重启
    // 哨兵节点可读可写
    @Test
    public void testOperateThenSentinelDownUp() {
        masterServer = RedisServer.builder().port(masterPort).build();
        slaveServer = RedisServer.builder().port(slavePort).replicaOf(masterPort).build();

        masterServer.start();
        slaveServer.start();

        redisSentinel =
                RedisSentinel.builder()
                        .sentinelPort(sentinelPort)
                        .masterPort(masterPort)
                        .masterName(masterName)
                        .downAfterMilliseconds((Long) 1000L)
                        .failoverTimeout((Long) 1000L)
                        .quorumSize((Integer) 1)
                        .build();
        redisSentinel.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisSentinel);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        // 哨兵节点宕机
        redisSentinel.stop();
        TimeTool.sleep(5000);
        // 哨兵节点重启
        redisSentinel.start();
        TimeTool.sleep(5000);

        // 重新获取链接
        Jedis newSentinelJedis = sentinelPool.getResource();

        writeSuccess(newSentinelJedis);
        readSuccess(newSentinelJedis);

        sentinelPool.close();
        slaveServer.stop();
        masterServer.stop();
        redisSentinel.stop();
    }
}
