package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//集群模式
@Slf4j
@NotThreadSafe
public class ModeClusterTest extends JedisClusterBaseTest {
    private int slavePort1;
    private String slaveHost1;

    private int slavePort2;
    private String slaveHost2;

    private int slavePort3;
    private String slaveHost3;

    private int slavePort4;
    private String slaveHost4;

    private int slavePort5;
    private String slaveHost5;

    private int slavePort6;
    private String slaveHost6;

    private int slavePort7;
    private String slaveHost7;


    @BeforeEach
    public void setUp() {
        super.setUp();
        slaveHost1 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort1 = RandomUtils.nextInt(10001, 11000);
        slaveHost2 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort2 = RandomUtils.nextInt(11001, 12000);
        slaveHost3 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort3 = RandomUtils.nextInt(12001, 13000);
        slaveHost4 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort4 = RandomUtils.nextInt(13001, 14000);
        slaveHost5 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort5 = RandomUtils.nextInt(14001, 15000);
        slaveHost6 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort6 = RandomUtils.nextInt(15001, 16000);
        slaveHost7 = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort7 = RandomUtils.nextInt(16001, 17000);
    }


    //一共三个节点  三个主节点 没有从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersNoSlavesCluster() {
        List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .serverPorts(slavePorts)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(10000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(slaveHost1, slavePort1));
        nodes.add(new HostAndPort(slaveHost2, slavePort2));
        nodes.add(new HostAndPort(slaveHost3, slavePort3));

        JedisCluster jedisCluster = new JedisCluster(nodes);

        //从主节点读取数据成功
        writeSuccess(jedisCluster);
        readSuccess(jedisCluster);

        redisCluster.stop();
    }

    //一共四个节点  三个主节点 一个从节点
    //无法满足需求，创建集群失败
    @Test
    public void shouldFailWhenThreeMastersOneSlaves() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3, slavePort4);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .serverPorts(slavePorts)
                                    .clusterReplicas(1)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }

    //一共五个节点  三个主节点 两个从节点
    //无法满足需求，创建集群失败
    @Test
    public void shouldFailWhenThreeMastersTwoSlaves() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3, slavePort4, slavePort5);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .serverPorts(slavePorts)
                                    .clusterReplicas(1)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }

    //一共六个节点  三个主节点 三个从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersThreeSlavesCluster() {
        List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3, slavePort4, slavePort5, slavePort6);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .serverPorts(slavePorts)
                        .clusterReplicas(1)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(10000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(slaveHost1, slavePort1));
        nodes.add(new HostAndPort(slaveHost2, slavePort2));
        nodes.add(new HostAndPort(slaveHost3, slavePort3));
        nodes.add(new HostAndPort(slaveHost4, slavePort4));
        nodes.add(new HostAndPort(slaveHost5, slavePort5));
        nodes.add(new HostAndPort(slaveHost6, slavePort6));

        JedisCluster jedisCluster = new JedisCluster(nodes);

        writeSuccess(jedisCluster);
        readSuccess(jedisCluster);

        redisCluster.stop();
    }

    //一共七个节点  个主节点 四个从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersFourSlavesCluster() {
        List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3, slavePort4, slavePort5, slavePort6, slavePort7);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .serverPorts(slavePorts)
                        .clusterReplicas(1)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(10000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(slaveHost1, slavePort1));
        nodes.add(new HostAndPort(slaveHost2, slavePort2));
        nodes.add(new HostAndPort(slaveHost3, slavePort3));
        nodes.add(new HostAndPort(slaveHost4, slavePort4));
        nodes.add(new HostAndPort(slaveHost5, slavePort5));
        nodes.add(new HostAndPort(slaveHost6, slavePort6));
        nodes.add(new HostAndPort(slaveHost7, slavePort7));

        JedisCluster jedisCluster = new JedisCluster(nodes);

        writeSuccess(jedisCluster);
        readSuccess(jedisCluster);

        redisCluster.stop();
    }


    //一共七个节点  三个主节点 四个从节点
    //无法满足需求，创建集群失败
    @Test
    public void shouldFailWhenThreeMastersFourSlavesCluster() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    List<Integer> slavePorts = List.of(slavePort1, slavePort2, slavePort3, slavePort4, slavePort5, slavePort6, slavePort7);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .serverPorts(slavePorts)
                                    .clusterReplicas(2)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }

    //一共一个节点
    // redis 集群至少需要三个节点
    @Test
    public void shouldFailWhenTwoNodes() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    List<Integer> slavePorts = List.of(slavePort1, slavePort2);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .serverPorts(slavePorts)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }

    //一共两个节点
    // redis 集群至少需要三个节点
    @Test
    public void shouldFailWhenOneNodes() {
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    List<Integer> slavePorts = List.of(slavePort1);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .serverPorts(slavePorts)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }
}
