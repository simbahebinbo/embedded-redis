package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.TimeTool;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisGatherTest {

    private RedisGather redisGather;

    private int masterPort;

    private String masterHost;

    private int slavePort;

    private String slaveHost;


    @BeforeEach
    public void setUp() {
        masterHost = CommonConstant.DEFAULT_REDIS_HOST;
        masterPort = RandomUtils.nextInt(10001, 11000);
        slaveHost = CommonConstant.DEFAULT_REDIS_HOST;
        slavePort = RandomUtils.nextInt(11001, 12000);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        redisGather.start();
        TimeTool.sleep(1000L);
        redisGather.stop();
    }

    @Test
    public void shouldNotAllowMultipleRunsWithoutStop() {

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    Set<Integer> slavePorts = Set.of(slavePort);

                    redisGather = RedisGather.builder()
                            .serverPorts(masterPort, slavePorts)
                            .replicationGroup(1)
                            .build();
                    redisGather.start();
                    redisGather.start();
                    redisGather.stop();
                });
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        redisGather.start();
        redisGather.stop();

        redisGather.start();
        redisGather.stop();

        redisGather.start();
        redisGather.stop();
    }

    @Test
    public void shouldIndicateInactiveBeforeStart() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        Assertions.assertFalse(redisGather.isActive());
    }

    @Test
    public void testPorts() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        Assertions.assertEquals(redisGather.ports(), Set.of(masterPort, slavePorts));
    }

    @Test
    public void shouldIndicateActiveAfterStart() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        redisGather.start();
        Assertions.assertTrue(redisGather.isActive());
        redisGather.stop();
    }

    @Test
    public void shouldIndicateInactiveAfterStop() {
        Set<Integer> slavePorts = Set.of(slavePort);

        redisGather = RedisGather.builder()
                .serverPorts(masterPort, slavePorts)
                .replicationGroup(1)
                .build();
        redisGather.start();
        redisGather.stop();
        Assertions.assertFalse(redisGather.isActive());
    }
}
