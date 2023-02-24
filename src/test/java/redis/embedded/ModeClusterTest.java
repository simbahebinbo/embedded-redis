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
import java.util.Set;

//集群模式
@Slf4j
@NotThreadSafe
public class ModeClusterTest extends JedisClusterBaseTest {
    private int nodePort1;
    private String nodeHost1;

    private int nodePort2;
    private String nodeHost2;

    private int nodePort3;
    private String nodeHost3;

    private int nodePort4;
    private String nodeHost4;

    private int nodePort5;
    private String nodeHost5;

    private int nodePort6;
    private String nodeHost6;

    private int nodePort7;
    private String nodeHost7;


    @BeforeEach
    public void setUp() {
        super.setUp();
        nodeHost1 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort1 = RandomUtils.nextInt(10001, 11000);
        nodeHost2 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort2 = RandomUtils.nextInt(11001, 12000);
        nodeHost3 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort3 = RandomUtils.nextInt(12001, 13000);
        nodeHost4 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort4 = RandomUtils.nextInt(13001, 14000);
        nodeHost5 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort5 = RandomUtils.nextInt(14001, 15000);
        nodeHost6 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort6 = RandomUtils.nextInt(15001, 16000);
        nodeHost7 = CommonConstant.DEFAULT_REDIS_HOST;
        nodePort7 = RandomUtils.nextInt(16001, 17000);
    }


    //一共三个节点  三个主节点 没有从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersNoSlavesCluster() {
        Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .nodePorts(nodePorts)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(30000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(nodeHost1, nodePort1));
        nodes.add(new HostAndPort(nodeHost2, nodePort2));
        nodes.add(new HostAndPort(nodeHost3, nodePort3));

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
                    Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3, nodePort4);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .nodePorts(nodePorts)
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
                    Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3, nodePort4, nodePort5);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .nodePorts(nodePorts)
                                    .clusterReplicas(1)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }

    //一共六个节点  三个主节点 三个从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersThreeSlavesCluster() {
        Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3, nodePort4, nodePort5, nodePort6);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .nodePorts(nodePorts)
                        .clusterReplicas(1)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(30000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(nodeHost1, nodePort1));
        nodes.add(new HostAndPort(nodeHost2, nodePort2));
        nodes.add(new HostAndPort(nodeHost3, nodePort3));
        nodes.add(new HostAndPort(nodeHost4, nodePort4));
        nodes.add(new HostAndPort(nodeHost5, nodePort5));
        nodes.add(new HostAndPort(nodeHost6, nodePort6));

        JedisCluster jedisCluster = new JedisCluster(nodes);

        writeSuccess(jedisCluster);
        readSuccess(jedisCluster);

        redisCluster.stop();
    }

    //一共七个节点  个主节点 四个从节点
    @Test
    public void testSimpleOperationsAfterRunWithThreeMastersFourSlavesCluster() {
        Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3, nodePort4, nodePort5, nodePort6, nodePort7);

        RedisCluster redisCluster =
                RedisCluster.builder()
                        .nodePorts(nodePorts)
                        .clusterReplicas(1)
                        .build();
        redisCluster.start();
        // 等待主从同步
        TimeTool.sleep(30000);

        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort(nodeHost1, nodePort1));
        nodes.add(new HostAndPort(nodeHost2, nodePort2));
        nodes.add(new HostAndPort(nodeHost3, nodePort3));
        nodes.add(new HostAndPort(nodeHost4, nodePort4));
        nodes.add(new HostAndPort(nodeHost5, nodePort5));
        nodes.add(new HostAndPort(nodeHost6, nodePort6));
        nodes.add(new HostAndPort(nodeHost7, nodePort7));

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
                    Set<Integer> nodePorts = Set.of(nodePort1, nodePort2, nodePort3, nodePort4, nodePort5, nodePort6, nodePort7);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .nodePorts(nodePorts)
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
                    Set<Integer> nodePorts = Set.of(nodePort1, nodePort2);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .nodePorts(nodePorts)
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
                    Set<Integer> nodePorts = Set.of(nodePort1);

                    RedisCluster redisCluster =
                            RedisCluster.builder()
                                    .nodePorts(nodePorts)
                                    .build();
                    redisCluster.start();
                    redisCluster.stop();
                });
    }
}
