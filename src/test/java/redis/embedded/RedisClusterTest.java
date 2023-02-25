package redis.embedded;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import redis.embedded.util.TimeTool;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RedisClusterTest {
    private RedisCluster redisCluster;
    private RedisServer redisServer1;
    private RedisServer redisServer2;
    private RedisServer redisServer3;

    private RedisClient redisClient;


    @BeforeEach
    public void setUp() {
        redisServer1 = mock(RedisServer.class);
        redisServer2 = mock(RedisServer.class);
        redisServer3 = mock(RedisServer.class);
        redisClient = mock(RedisClient.class);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisCluster = new RedisCluster(redisServers, redisClient);
        redisCluster.start();
        TimeTool.sleep(1000L);
        redisCluster.stop();
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisCluster = new RedisCluster(redisServers, redisClient);
        redisCluster.start();
        redisCluster.stop();

        redisCluster.start();
        redisCluster.stop();

        redisCluster.start();
        redisCluster.stop();
    }


    //集群模式 集群停止
    @Test
    public void stopShouldStopEntireCluster() {

        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisCluster = new RedisCluster(redisServers, redisClient);

        redisCluster.stop();

        redisServers.forEach(redisServer -> verify(redisServer).stop());
    }

    //集群模式 集群启动
    @Test
    public void startShouldStartEntireCluster() {

        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisCluster = new RedisCluster(redisServers, redisClient);

        redisCluster.start();

        redisServers.forEach(redisServer -> verify(redisServer).start());
    }

    //集群模式 集群判活
    @Test
    public void isActiveShouldCheckEntireClusterIfAllActive() {
        given(redisServer1.isActive()).willReturn(true);
        given(redisServer2.isActive()).willReturn(true);
        given(redisServer3.isActive()).willReturn(true);

        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisCluster = new RedisCluster(redisServers, redisClient);

        redisCluster.isActive();

        redisServers.forEach(redisServer -> verify(redisServer).isActive());
    }
}
