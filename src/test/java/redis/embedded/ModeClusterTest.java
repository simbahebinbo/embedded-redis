package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.embedded.common.CommonConstant;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

//集群模式
@Slf4j
@NotThreadSafe
public class ModeClusterTest extends JedisClusterBaseTest {
//    private RedisCluster redisCluster;
//    private RedisServer redisServer1;
//    private RedisServer redisServer2;
//    private RedisServer redisServer3;

    private int slavePort1;
    private String slaveHost1;

    private int slavePort2;
    private String slaveHost2;

    private int slavePort3;
    private String slaveHost3;

    @BeforeEach
    public void setUp() {
        super.setUp();
//        masterName = RandomStringUtils.randomAlphabetic(50, 100);
//        masterName1 = RandomStringUtils.randomAlphabetic(50, 100);
//        masterName2 = RandomStringUtils.randomAlphabetic(50, 100);
//        masterName3 = RandomStringUtils.randomAlphabetic(50, 100);
        slaveHost1 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort1 = RandomUtils.nextInt(20000, 30000);
        slaveHost2 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort2 = RandomUtils.nextInt(30000, 40000);
        slaveHost3 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort3 = RandomUtils.nextInt(40000, 50000);
    }


    @Test
    public void testSimpleOperationsAfterRunWithSingleMasterNoSlavesCluster() throws Exception {
        List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3);

        //given
        RedisCluster redisCluster =
                RedisCluster.builder()
                        .serverPorts(slavePorts)
                        .build();
        redisCluster.start();


//        Set<HostAndPort> nodes = new HashSet<>();
//        nodes.add(new HostAndPort(slaveHost1, slavePort1));
//        nodes.add(new HostAndPort(slaveHost2, slavePort2));
//        nodes.add(new HostAndPort(slaveHost3, slavePort3));
//
//        JedisCluster jedisCluster = new JedisCluster(nodes);
//
//        writeSuccess(jedisCluster);
//        readSuccess(jedisCluster);
//
        redisCluster.stop();

//        //when
//        JedisSentinelPool pool = null;
//        Jedis jedis = null;
//        try {
//            pool = new JedisSentinelPool("ourmaster",
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT"));
//            jedis = testPool(pool);
//        } finally {
//            if (jedis != null)
//                pool.returnResource(jedis);
//            cluster.stop();
//        }
    }

//    @Test
//    public void testSimpleOperationsAfterRunWithSingleMasterAndOneSlave() throws Exception {
//        //given
//        final RedisCluster cluster =
//                RedisCluster.builder().sentinelCount(1).replicationGroup("ourmaster", 1).build();
//        cluster.start();
//
//        //when
//        JedisSentinelPool pool = null;
//        Jedis jedis = null;
//        try {
//            pool = new JedisSentinelPool("ourmaster",
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT"));
//            jedis = testPool(pool);
//        } finally {
//            if (jedis != null)
//                pool.returnResource(jedis);
//            cluster.stop();
//        }
//    }
//
//    @Test
//    public void testSimpleOperationsAfterRunWithSingleMasterMultipleSlaves() throws Exception {
//        //given
//        final RedisCluster cluster =
//                RedisCluster.builder().sentinelCount(1).replicationGroup("ourmaster", 2).build();
//        cluster.start();
//
//        //when
//        JedisSentinelPool pool = null;
//        Jedis jedis = null;
//        try {
//            pool = new JedisSentinelPool("ourmaster",
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT"));
//            jedis = testPool(pool);
//        } finally {
//            if (jedis != null)
//                pool.returnResource(jedis);
//            cluster.stop();
//        }
//    }
//
//    @Test
//    public void testSimpleOperationsAfterRunWithTwoSentinelsSingleMasterMultipleSlaves() throws
//            Exception {
//        //given
//        final RedisCluster cluster =
//                RedisCluster.builder().sentinelCount(2).replicationGroup("ourmaster", 2).build();
//        cluster.start();
//
//        //when
//        JedisSentinelPool pool = null;
//        Jedis jedis = null;
//        try {
//            pool = new JedisSentinelPool("ourmaster",
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT",
//                            "localhost:26380"));
//            jedis = testPool(pool);
//        } finally {
//            if (jedis != null)
//                pool.returnResource(jedis);
//            cluster.stop();
//        }
//    }
//
//    @Test
//    public void testSimpleOperationsAfterRunWithTwoPredefinedSentinelsSingleMasterMultipleSlaves()
//            throws Exception {
//        //given
//        List<Integer> sentinelPorts = Arrays.asList(26381, 26382);
//        final RedisCluster cluster =
//                RedisCluster.builder().sentinelPorts(sentinelPorts).replicationGroup("ourmaster", 2).build();
//        cluster.start();
//        final Set<String> sentinelHosts = JedisUtil.portsToJedisHosts(sentinelPorts);
//
//        //when
//        JedisSentinelPool pool = null;
//        Jedis jedis = null;
//        try {
//            pool = new JedisSentinelPool("ourmaster", sentinelHosts);
//            jedis = testPool(pool);
//        } finally {
//            if (jedis != null)
//                pool.returnResource(jedis);
//            cluster.stop();
//        }
//    }
//
//    @Test
//    public void
//    testSimpleOperationsAfterRunWithThreeSentinelsThreeMastersOneSlavePerMasterCluster() throws
//            Exception {
//        //given
//        final String master1 = "master1";
//        final String master2 = "master2";
//        final String master3 = "master3";
//        final RedisCluster cluster = RedisCluster.builder().sentinelCount(3).quorumSize(2)
//                .replicationGroup(master1, 1)
//                .replicationGroup(master2, 1)
//                .replicationGroup(master3, 1)
//                .build();
//        cluster.start();
//
//        //when
//        JedisSentinelPool pool1 = null;
//        JedisSentinelPool pool2 = null;
//        JedisSentinelPool pool3 = null;
//        Jedis jedis1 = null;
//        Jedis jedis2 = null;
//        Jedis jedis3 = null;
//        try {
//            pool1 = new JedisSentinelPool(master1,
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT",
//                            "localhost:26380", "localhost:26381"));
//            pool2 = new JedisSentinelPool(master2,
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT",
//                            "localhost:26380", "localhost:26381"));
//            pool3 = new JedisSentinelPool(master3,
//                    Sets.newHashSet("localhost:CommonConstant.DEFAULT_REDIS_SENTINEL_PORT",
//                            "localhost:26380", "localhost:26381"));
//            jedis1 = testPool(pool1);
//            jedis2 = testPool(pool2);
//            jedis3 = testPool(pool3);
//        } finally {
//            if (jedis1 != null)
//                pool1.returnResource(jedis1);
//            if (jedis2 != null)
//                pool2.returnResource(jedis2);
//            if (jedis3 != null)
//                pool3.returnResource(jedis3);
//            cluster.stop();
//        }
//    }
//
//    @Test
//    public void
//    testSimpleOperationsAfterRunWithThreeSentinelsThreeMastersOneSlavePerMasterEphemeralCluster()
//            throws Exception {
//        //given
//        final String master1 = "master1";
//        final String master2 = "master2";
//        final String master3 = "master3";
//        final RedisCluster cluster =
//                RedisCluster.builder().ephemeral().sentinelCount(3).quorumSize(2)
//                        .replicationGroup(master1, 1)
//                        .replicationGroup(master2, 1)
//                        .replicationGroup(master3, 1)
//                        .build();
//        cluster.start();
//        final Set<String> sentinelHosts = JedisUtil.sentinelHosts(cluster);
//
//        //when
//        JedisSentinelPool pool1 = null;
//        JedisSentinelPool pool2 = null;
//        JedisSentinelPool pool3 = null;
//        Jedis jedis1 = null;
//        Jedis jedis2 = null;
//        Jedis jedis3 = null;
//        try {
//            pool1 = new JedisSentinelPool(master1, sentinelHosts);
//            pool2 = new JedisSentinelPool(master2, sentinelHosts);
//            pool3 = new JedisSentinelPool(master3, sentinelHosts);
//            jedis1 = testPool(pool1);
//            jedis2 = testPool(pool2);
//            jedis3 = testPool(pool3);
//        } finally {
//            if (jedis1 != null)
//                pool1.returnResource(jedis1);
//            if (jedis2 != null)
//                pool2.returnResource(jedis2);
//            if (jedis3 != null)
//                pool3.returnResource(jedis3);
//            cluster.stop();
//        }
//    }
//
//    private Jedis testPool(JedisSentinelPool pool) {
//        Jedis jedis;
//        jedis = pool.getResource();
//        jedis.mset("abc", "1", "def", "2");
//
//        //then
//        assertEquals("1", jedis.mget("abc").get(0));
//        assertEquals("2", jedis.mget("def").get(0));
//        assertEquals(null, jedis.mget("xyz").get(0));
//        return jedis;
//    }
}
