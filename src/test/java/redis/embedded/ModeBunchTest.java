package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.JedisUtil;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Set;

// 集合模式
@Slf4j
@NotThreadSafe
public class ModeBunchTest extends JedisBaseTest {
    private Integer sentinelPort;
    private String sentinelHost;

    private Integer serverPort;
    private String serverHost;
    private String masterName;
    private String masterName1;
    private String masterName2;
    private String masterName3;

    @BeforeEach
    public void setUp() {
        super.setUp();
        masterName = RandomStringUtils.secure().nextAlphabetic(50, 100);
        masterName1 = RandomStringUtils.secure().nextAlphabetic(50, 100);
        masterName2 = RandomStringUtils.secure().nextAlphabetic(50, 100);
        masterName3 = RandomStringUtils.secure().nextAlphabetic(50, 100);
        sentinelHost = CommonConstant.DEFAULT_REDIS_HOST;
        sentinelPort = (Integer) RandomUtils.secure().randomInt(10001, 11000);
        serverHost = CommonConstant.DEFAULT_REDIS_HOST;
        serverPort = (Integer) RandomUtils.secure().randomInt(11001, 12000);
    }

    @Test
    public void testSimpleOperationsAfterRunWithSingleMasterNoSlave() {
        Set<Integer> sentinelPorts = Set.of(sentinelPort);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .replicationGroup(masterName, (Integer) 0)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisBunch.stop();
    }

    @Test
    public void testSimpleOperationsAfterRunWithSingleMasterSingleSlave() {
        Set<Integer> sentinelPorts = Set.of(sentinelPort);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .replicationGroup(masterName, (Integer) 1)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisBunch.stop();
    }

    @Test
    public void testSimpleOperationsAfterRunWithSingleMasterMultipleSlaves() {
        Set<Integer> sentinelPorts = Set.of(sentinelPort);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .replicationGroup(masterName, (Integer) 2)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisBunch.stop();
    }

    @Test
    public void testSimpleOperationsAfterRunWithTwoSentinelsSingleMasterMultipleSlaves() {
        Integer sentinelPort1 = sentinelPort;
        Integer sentinelPort2 = (Integer) (sentinelPort + 1);

        Set<Integer> sentinelPorts = Set.of(sentinelPort1, sentinelPort2);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .replicationGroup(masterName, (Integer) 2)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisBunch.stop();
    }

    @Test
    public void testSimpleOperationsAfterRunWithTwoPredefinedSentinelsSingleMasterMultipleSlaves() {
        Integer sentinelPort1 = sentinelPort;
        Integer sentinelPort2 = (Integer) (sentinelPort + 1);

        Set<Integer> sentinelPorts = Set.of(sentinelPort1, sentinelPort2);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .replicationGroup(masterName, (Integer) 2)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool = new JedisSentinelPool(masterName, sentinelJedisHosts);
        Jedis sentinelJedis = sentinelPool.getResource();

        writeSuccess(sentinelJedis);
        readSuccess(sentinelJedis);

        sentinelPool.close();
        redisBunch.stop();
    }

    @Test
    public void testSimpleOperationsAfterRunWithThreeSentinelsThreeMastersSingleSlavePerMaster() {
        Integer sentinelPort1 = sentinelPort;
        Integer sentinelPort2 = (Integer) (sentinelPort + 1);
        Integer sentinelPort3 = (Integer) (sentinelPort + 2);

        Set<Integer> sentinelPorts = Set.of(sentinelPort1, sentinelPort2, sentinelPort3);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .sentinelPorts(sentinelPorts)
                        .quorumSize((Integer) 2)
                        .replicationGroup(masterName1, (Integer) 1)
                        .replicationGroup(masterName2, (Integer) 1)
                        .replicationGroup(masterName3, (Integer) 1)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool1 = new JedisSentinelPool(masterName1, sentinelJedisHosts);
        JedisSentinelPool sentinelPool2 = new JedisSentinelPool(masterName2, sentinelJedisHosts);
        JedisSentinelPool sentinelPool3 = new JedisSentinelPool(masterName3, sentinelJedisHosts);

        Jedis sentinelJedis1 = sentinelPool1.getResource();
        Jedis sentinelJedis2 = sentinelPool2.getResource();
        Jedis sentinelJedis3 = sentinelPool3.getResource();

        writeSuccess(sentinelJedis1);
        readSuccess(sentinelJedis1);
        writeSuccess(sentinelJedis2);
        readSuccess(sentinelJedis2);
        writeSuccess(sentinelJedis3);
        readSuccess(sentinelJedis3);

        sentinelPool1.close();
        sentinelPool2.close();
        sentinelPool3.close();
        redisBunch.stop();
    }

    @Test
    public void
    testSimpleOperationsAfterRunWithThreeSentinelsThreeMastersSingleSlavePerMasterEphemeral() {
        Integer sentinelPort1 = sentinelPort;
        Integer sentinelPort2 = (Integer) (sentinelPort + 1);
        Integer sentinelPort3 = (Integer) (sentinelPort + 2);

        Set<Integer> sentinelPorts = Set.of(sentinelPort1, sentinelPort2, sentinelPort3);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .ephemeral()
                        .sentinelPorts(sentinelPorts)
                        .quorumSize((Integer) 2)
                        .replicationGroup(masterName1, (Integer) 1)
                        .replicationGroup(masterName2, (Integer) 1)
                        .replicationGroup(masterName3, (Integer) 1)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool1 = new JedisSentinelPool(masterName1, sentinelJedisHosts);
        JedisSentinelPool sentinelPool2 = new JedisSentinelPool(masterName2, sentinelJedisHosts);
        JedisSentinelPool sentinelPool3 = new JedisSentinelPool(masterName3, sentinelJedisHosts);

        Jedis sentinelJedis1 = sentinelPool1.getResource();
        Jedis sentinelJedis2 = sentinelPool2.getResource();
        Jedis sentinelJedis3 = sentinelPool3.getResource();

        writeSuccess(sentinelJedis1);
        readSuccess(sentinelJedis1);
        writeSuccess(sentinelJedis2);
        readSuccess(sentinelJedis2);
        writeSuccess(sentinelJedis3);
        readSuccess(sentinelJedis3);

        sentinelPool1.close();
        sentinelPool2.close();
        sentinelPool3.close();
        redisBunch.stop();
    }


    @Test
    public void
    testSimpleOperationsAfterRunWithThreeSentinelsThreeMastersTwoSlavePerMaster() {
        Integer sentinelPort1 = sentinelPort;
        Integer sentinelPort2 = (Integer) (sentinelPort + 1);
        Integer sentinelPort3 = (Integer) (sentinelPort + 2);

        Integer serverPort1 = serverPort;
        Integer serverPort2 = (Integer) (serverPort + 1);
        Integer serverPort3 = (Integer) (serverPort + 2);
        Integer serverPort4 = (Integer) (serverPort + 3);
        Integer serverPort5 = (Integer) (serverPort + 4);
        Integer serverPort6 = (Integer) (serverPort + 5);
        Integer serverPort7 = (Integer) (serverPort + 6);
        Integer serverPort8 = (Integer) (serverPort + 7);
        Integer serverPort9 = (Integer) (serverPort + 8);


        Set<Integer> sentinelPorts = Set.of(sentinelPort1, sentinelPort2, sentinelPort3);
        final Set<Integer> serverPorts1 = Set.of(serverPort1, serverPort2, serverPort3);
        final Set<Integer> serverPorts2 = Set.of(serverPort4, serverPort5, serverPort6);
        final Set<Integer> serverPorts3 = Set.of(serverPort7, serverPort8, serverPort9);

        RedisBunch redisBunch =
                RedisBunch.builder()
                        .ephemeral()
                        .sentinelPorts(sentinelPorts)
                        .quorumSize((Integer) 2)
                        .serverPorts(serverPorts1).replicationGroup(masterName1, (Integer) 2)
                        .serverPorts(serverPorts2).replicationGroup(masterName2, (Integer) 2)
                        .serverPorts(serverPorts3).replicationGroup(masterName3, (Integer) 2)
                        .build();
        redisBunch.start();

        Set<String> sentinelJedisHosts = JedisUtil.sentinelJedisHosts(redisBunch);
        JedisSentinelPool sentinelPool1 = new JedisSentinelPool(masterName1, sentinelJedisHosts);
        JedisSentinelPool sentinelPool2 = new JedisSentinelPool(masterName2, sentinelJedisHosts);
        JedisSentinelPool sentinelPool3 = new JedisSentinelPool(masterName3, sentinelJedisHosts);

        Jedis sentinelJedis1 = sentinelPool1.getResource();
        Jedis sentinelJedis2 = sentinelPool2.getResource();
        Jedis sentinelJedis3 = sentinelPool3.getResource();

        writeSuccess(sentinelJedis1);
        readSuccess(sentinelJedis1);
        writeSuccess(sentinelJedis2);
        readSuccess(sentinelJedis2);
        writeSuccess(sentinelJedis3);
        readSuccess(sentinelJedis3);

        sentinelPool1.close();
        sentinelPool2.close();
        sentinelPool3.close();
        redisBunch.stop();
    }
}
