package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Set;

// 主从模式
@Slf4j
@NotThreadSafe
public class ModeGatherTest extends JedisBaseTest {
    private RedisGather redisGather;

    private Integer masterPort;

    private String masterHost;

    private Integer slavePort;

    private String slaveHost;

    private Integer slavePort1;

    private String slaveHost1;

    private Integer slavePort2;

    private String slaveHost2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        masterHost = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort = (Integer) RandomUtils.secure().randomInt(10001, 11000);
        slaveHost = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort = (Integer) RandomUtils.secure().randomInt(11001, 12000);
        slaveHost1 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort1 = (Integer) RandomUtils.secure().randomInt(12001, 13000);
        slaveHost2 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort2 = (Integer) RandomUtils.secure().randomInt(13001, 14000);
    }

    // 主从模式
    // 正常启动
    // 主节点可读可写 从节点可读不可写
    @Test
    public void testOperateAfterRunWithSingleMasterSingleSlave() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();

        redisGather.start();

        JedisPool masterPool = new JedisPool(masterHost, masterPort);
        JedisPool slavePool = new JedisPool(slaveHost, slavePort);

        Jedis masterJedis = masterPool.getResource();
        Jedis slaveJedis = slavePool.getResource();
        //读写主节点成功
        writeSuccess(masterJedis);
        readSuccess(masterJedis);
        // 等待主从同步
        TimeTool.sleep(10000);
        //读取主节点写入的值
        readSuccess(slaveJedis);
        //写入从节点失败
        writeFail(slaveJedis);
        //读取从节点成功
        readNothing(slaveJedis);

        masterPool.close();
        slavePool.close();
        redisGather.stop();
    }

    // 主从模式
    // 正常启动
    // 主节点可读可写 从节点可读不可写
    @Test
    public void testOperateAfterRunWithSingleMasterMultipleSlaves() {
        Set<Integer> slavePorts = Set.of(slavePort1, slavePort2);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(2)
                .build();

        redisGather.start();

        JedisPool masterPool = new JedisPool(masterHost, masterPort);
        JedisPool slavePool1 = new JedisPool(slaveHost1, slavePort1);
        JedisPool slavePool2 = new JedisPool(slaveHost2, slavePort2);

        Jedis masterJedis = masterPool.getResource();
        Jedis slaveJedis1 = slavePool1.getResource();
        Jedis slaveJedis2 = slavePool2.getResource();

        //读写主节点成功
        writeSuccess(masterJedis);
        readSuccess(masterJedis);
        // 等待主从同步
        TimeTool.sleep(10000);
        //读取主节点写入的值
        readSuccess(slaveJedis1);
        readSuccess(slaveJedis2);

        //写入从节点失败
        writeFail(slaveJedis1);
        writeFail(slaveJedis2);

        //读取从节点成功
        readNothing(slaveJedis1);
        readNothing(slaveJedis2);

        masterPool.close();
        slavePool1.close();
        slavePool2.close();

        redisGather.stop();
    }


    // 主从模式
    // 正常启动
    // 节点宕机
    // 主节点不可读不可写 从节点不可读不可写
    @Test
    public void testOperateThenDown() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();

        redisGather.start();

        JedisPool masterPool = new JedisPool(masterHost, masterPort);
        JedisPool slavePool = new JedisPool(slaveHost, slavePort);

        Jedis masterJedis = masterPool.getResource();
        Jedis slaveJedis = slavePool.getResource();

        //读写主节点成功
        writeSuccess(masterJedis);
        readSuccess(masterJedis);
        // 等待主从同步
        TimeTool.sleep(10000);
        //读取主节点写入的值
        readSuccess(slaveJedis);

        //写入从节点失败
        writeFail(slaveJedis);
        //读取从节点成功
        readNothing(slaveJedis);

        // 节点宕机
        redisGather.stop();
        TimeTool.sleep(3000);

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接 失败
                    Jedis newMasterJedis = masterPool.getResource();
                });

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接 失败
                    Jedis newSlaveJedis = slavePool.getResource();
                });

        masterPool.close();
        slavePool.close();

        redisGather.stop();
    }

    // 主从模式
    // 正常启动
    // 节点宕机，然后重启
    // 主节点可读可写 从节点可读不可写
    @Test
    public void testOperateThenDownUp() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();

        redisGather.start();

        JedisPool masterPool = new JedisPool(masterHost, masterPort);
        JedisPool slavePool = new JedisPool(slaveHost, slavePort);

        Jedis masterJedis = masterPool.getResource();
        Jedis slaveJedis = slavePool.getResource();

        //读写主节点成功
        writeSuccess(masterJedis);
        readSuccess(masterJedis);
        // 等待主从同步
        TimeTool.sleep(10000);
        //读取主节点写入的值
        readSuccess(slaveJedis);
        //写入从节点失败
        writeFail(slaveJedis);
        //读取从节点成功
        readNothing(slaveJedis);

        // 节点宕机
        redisGather.stop();
        TimeTool.sleep(3000);
        // 节点重启
        redisGather.start();
        TimeTool.sleep(3000);

        // 重新获取连接
        Jedis newMasterJedis = masterPool.getResource();
        Jedis newSlaveJedis = slavePool.getResource();

        //读写主节点成功
        writeSuccess(newMasterJedis);
        readSuccess(newMasterJedis);
        // 等待主从同步
        TimeTool.sleep(10000);
        //读取主节点写入的值
        readSuccess(newSlaveJedis);
        //写入从节点失败
        writeFail(newSlaveJedis);
        //读取从节点成功
        readNothing(newSlaveJedis);

        masterPool.close();
        slavePool.close();
        redisGather.stop();
    }
}
