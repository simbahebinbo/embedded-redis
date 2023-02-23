package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.embedded.common.CommonConstant;

// 单机模式
@Slf4j
public class ModeStandaloneTest extends JedisBaseTest {

    private RedisServer redisServer;
    private int port;

    private String host;

    @BeforeEach
    public void setUp() {
        super.setUp();
        host = CommonConstant.DEFAULT_REDIS_HOST;
        port = RandomUtils.nextInt(10001, 11000);
    }

    // 单机模式
    // 正常启动
    // 节点可读可写
    @Test
    public void testOperate() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        JedisPool pool = new JedisPool(host, port);

        Jedis jedis = pool.getResource();
        writeSuccess(jedis);
        readSuccess(jedis);

        pool.close();
        redisServer.stop();
    }

    // 单机模式
    // 正常启动
    // 节点宕机
    // 节点不可读不可写
    @Test
    public void testOperateThenStandaloneDown() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        JedisPool pool = new JedisPool(host, port);
        Jedis jedis = pool.getResource();
        writeSuccess(jedis);
        readSuccess(jedis);

        redisServer.stop();

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    // 重新获取连接
                    Jedis newJedis = pool.getResource();
                });

        pool.close();
        redisServer.stop();
    }

    // 单机模式
    // 正常启动
    // 节点宕机，然后重启
    // 节点可读可写
    @Test
    public void testOperateThenStandaloneDownUp() {
        redisServer = RedisServer.builder().port(port).build();
        redisServer.start();
        JedisPool pool = new JedisPool(host, port);
        Jedis jedis = pool.getResource();
        writeSuccess(jedis);
        readSuccess(jedis);

        redisServer.stop();
        redisServer.start();

        // 重新获取连接
        Jedis newJedis = pool.getResource();
        writeSuccess(newJedis);
        readSuccess(newJedis);

        pool.close();
        redisServer.stop();
    }
}
