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

public class RedisBunchTest {
    private RedisSentinel redisSentinel1;
    private RedisSentinel redisSentinel2;
    private RedisServer redisServer1;
    private RedisServer redisServer2;

    private RedisBunch redisBunch;

    @BeforeEach
    public void setUp() {
        redisSentinel1 = mock(RedisSentinel.class);
        redisSentinel2 = mock(RedisSentinel.class);
        redisServer1 = mock(RedisServer.class);
        redisServer2 = mock(RedisServer.class);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisBunch = new RedisBunch(redisSentinels, redisServers);
        redisBunch.start();
        TimeTool.sleep(1000L);
        redisBunch.stop();
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisBunch = new RedisBunch(redisSentinels, redisServers);
        redisBunch.start();
        redisBunch.stop();

        redisBunch.start();
        redisBunch.stop();

        redisBunch.start();
        redisBunch.stop();
    }


    //哨兵模式 集合停止
    @Test
    public void stopShouldStopEntireBunch() {

        List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisBunch = new RedisBunch(redisSentinels, redisServers);

        redisBunch.stop();

        redisSentinels.forEach(redisSentinel -> verify(redisSentinel).stop());
        redisServers.forEach(redisServer -> verify(redisServer).stop());
    }

    //哨兵模式 集合启动
    @Test
    public void startShouldStartEntireBunch() {
        List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisBunch = new RedisBunch(redisSentinels, redisServers);

        redisBunch.start();

        redisSentinels.forEach(redisSentinel -> verify(redisSentinel).start());
        redisServers.forEach(redisServer -> verify(redisServer).start());
    }

    //哨兵模式 集合判活
    @Test
    public void isActiveShouldCheckEntireBunchIfAllActive() {
        given(redisSentinel1.isActive()).willReturn(true);
        given(redisSentinel2.isActive()).willReturn(true);
        given(redisServer1.isActive()).willReturn(true);
        given(redisServer2.isActive()).willReturn(true);
        List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisBunch = new RedisBunch(redisSentinels, redisServers);

        redisBunch.isActive();

        redisSentinels.forEach(redisSentinel -> verify(redisSentinel).isActive());
        redisServers.forEach(redisServer -> verify(redisServer).isActive());
    }
}
