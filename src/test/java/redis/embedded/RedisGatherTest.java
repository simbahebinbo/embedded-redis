package redis.embedded;

import lombok.extern.slf4j.Slf4j;
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

//主从模式
@Slf4j
public class RedisGatherTest {

    private RedisGather redisGather;

    private RedisServer redisServer1;
    private RedisServer redisServer2;


    @BeforeEach
    public void setUp() {
        redisServer1 = mock(RedisServer.class);
        redisServer2 = mock(RedisServer.class);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisGather = new RedisGather(redisServers);
        redisGather.start();
        TimeTool.sleep(1000L);
        redisGather.stop();
    }


    @Test
    public void shouldAllowSubsequentRuns() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisGather = new RedisGather(redisServers);
        redisGather.start();
        redisGather.stop();

        redisGather.start();
        redisGather.stop();

        redisGather.start();
        redisGather.stop();
    }

    @Test
    public void stopShouldStopEntireGather() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisGather = new RedisGather(redisServers);

        redisGather.stop();

        redisServers.forEach(redisServer -> verify(redisServer).stop());
    }

    @Test
    public void startShouldStartEntireGather() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisGather = new RedisGather(redisServers);

        redisGather.start();

        redisServers.forEach(redisServer -> verify(redisServer).start());
    }

    @Test
    public void isActiveShouldCheckEntireGatherIfAllActive() {
        given(redisServer1.isActive()).willReturn(Boolean.TRUE);
        given(redisServer2.isActive()).willReturn(Boolean.TRUE);
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
        redisGather = new RedisGather(redisServers);

        redisGather.isActive();

        redisServers.forEach(redisServer -> verify(redisServer).isActive());
    }
}
