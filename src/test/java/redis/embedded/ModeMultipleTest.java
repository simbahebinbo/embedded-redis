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

// 主主模式
@Slf4j
@NotThreadSafe
public class ModeMultipleTest extends JedisBaseTest {
    private RedisMultiple redisMultiple;

    private int masterPort1;

    private String masterHost1;

    private int masterPort2;

    private String masterHost2;

    private int masterPort3;

    private String masterHost3;


    @BeforeEach
    public void setUp() {
        super.setUp();
        masterHost1 = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort1 = RandomUtils.nextInt(10001, 11000);
        masterHost2 = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort2 = RandomUtils.nextInt(11001, 12000);
        masterHost3 = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort3 = RandomUtils.nextInt(12001, 13000);
    }

    // 主主模式
    // 正常启动
    @Test
    public void testOperateAfterRunWithMultipleMasters() {
        Set<Integer> masterPorts = Set.of(masterPort1, masterPort2, masterPort3);

        redisMultiple = RedisMultiple.builder()
                .masterPorts(masterPorts)
                .build();

        redisMultiple.start();

        JedisPool masterPool1 = new JedisPool(masterHost1, masterPort1);
        JedisPool masterPool2 = new JedisPool(masterHost2, masterPort2);
        JedisPool masterPool3 = new JedisPool(masterHost3, masterPort3);

        Jedis masterJedis1 = masterPool1.getResource();
        Jedis masterJedis2 = masterPool2.getResource();
        Jedis masterJedis3 = masterPool3.getResource();

        //读写主节点成功
        writeSuccess(masterJedis1);
        readSuccess(masterJedis1);

        writeSuccess(masterJedis2);
        readSuccess(masterJedis2);

        writeSuccess(masterJedis3);
        readSuccess(masterJedis3);

        masterPool1.close();
        masterPool2.close();
        masterPool3.close();

        redisMultiple.stop();
    }


    // 主主模式
    // 正常启动
    // 主节点宕机
    // 主节点不可读不可写
    @Test
    public void testOperateThenMastersDown() {
        Set<Integer> masterPorts = Set.of(masterPort1, masterPort2, masterPort3);

        redisMultiple = RedisMultiple.builder()
                .masterPorts(masterPorts)
                .build();

        redisMultiple.start();

        JedisPool masterPool1 = new JedisPool(masterHost1, masterPort1);
        JedisPool masterPool2 = new JedisPool(masterHost2, masterPort2);
        JedisPool masterPool3 = new JedisPool(masterHost3, masterPort3);

        Jedis masterJedis1 = masterPool1.getResource();
        Jedis masterJedis2 = masterPool2.getResource();
        Jedis masterJedis3 = masterPool3.getResource();

        //读写主节点成功
        writeSuccess(masterJedis1);
        readSuccess(masterJedis1);

        writeSuccess(masterJedis2);
        readSuccess(masterJedis2);

        writeSuccess(masterJedis3);
        readSuccess(masterJedis3);

        // 主节点宕机
        redisMultiple.stop();
        TimeTool.sleep(3000);

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接 失败
                    Jedis newMasterJedis1 = masterPool1.getResource();
                });

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接 失败
                    Jedis newMasterJedis2 = masterPool2.getResource();
                });

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接 失败
                    Jedis newMasterJedis3 = masterPool3.getResource();
                });

        masterPool1.close();
        masterPool2.close();
        masterPool3.close();

        redisMultiple.stop();
    }

    // 主主模式
    // 正常启动
    // 主节点宕机，然后重启
    // 主节点可读可写
    @Test
    public void testOperateThenMastersDownUp() {
        Set<Integer> masterPorts = Set.of(masterPort1, masterPort2, masterPort3);

        redisMultiple = RedisMultiple.builder()
                .masterPorts(masterPorts)
                .build();

        redisMultiple.start();

        JedisPool masterPool1 = new JedisPool(masterHost1, masterPort1);
        JedisPool masterPool2 = new JedisPool(masterHost2, masterPort2);
        JedisPool masterPool3 = new JedisPool(masterHost3, masterPort3);

        Jedis masterJedis1 = masterPool1.getResource();
        Jedis masterJedis2 = masterPool2.getResource();
        Jedis masterJedis3 = masterPool3.getResource();

        //读写主节点成功
        writeSuccess(masterJedis1);
        readSuccess(masterJedis1);

        writeSuccess(masterJedis2);
        readSuccess(masterJedis2);

        writeSuccess(masterJedis3);
        readSuccess(masterJedis3);


        // 主节点宕机
        redisMultiple.stop();
        TimeTool.sleep(3000);
        // 主节点重启
        redisMultiple.start();
        TimeTool.sleep(3000);

        // 重新获取连接
        Jedis newMasterJedis1 = masterPool1.getResource();
        Jedis newMasterJedis2 = masterPool2.getResource();
        Jedis newMasterJedis3 = masterPool3.getResource();

        //读写主节点成功
        writeSuccess(newMasterJedis1);
        readSuccess(newMasterJedis1);

        writeSuccess(newMasterJedis2);
        readSuccess(newMasterJedis2);

        writeSuccess(newMasterJedis3);
        readSuccess(newMasterJedis3);

        masterPool1.close();
        masterPool2.close();
        masterPool3.close();

        redisMultiple.stop();
    }
}
