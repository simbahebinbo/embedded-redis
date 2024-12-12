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

@Slf4j
public class RedisMultipleTest {

    private RedisMultiple redisMultiple;

    private RedisServer redisServer1;
    private RedisServer redisServer2;
    private RedisServer redisServer3;


    @BeforeEach
    public void setUp() {
        redisServer1 = mock(RedisServer.class);
        redisServer2 = mock(RedisServer.class);
        redisServer3 = mock(RedisServer.class);
    }

    @Test
    @Timeout(value = 3000, unit = TimeUnit.MILLISECONDS)
    public void testSimpleRun() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisMultiple = new RedisMultiple(redisServers);
        redisMultiple.start();
        TimeTool.sleep(1000L);
        redisMultiple.stop();
    }

    @Test
    public void shouldAllowSubsequentRuns() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisMultiple = new RedisMultiple(redisServers);
        redisMultiple.start();
        redisMultiple.stop();

        redisMultiple.start();
        redisMultiple.stop();

        redisMultiple.start();
        redisMultiple.stop();
    }


    //集群模式 集群停止
    @Test
    public void stopShouldStopEntireMultiple() {

        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisMultiple = new RedisMultiple(redisServers);

        redisMultiple.stop();

        redisServers.forEach(redisServer -> verify(redisServer).stop());
    }

    //集群模式 集群启动
    @Test
    public void startShouldStartEntireMultiple() {
        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisMultiple = new RedisMultiple(redisServers);

        redisMultiple.start();

        redisServers.forEach(redisServer -> verify(redisServer).start());
    }

    //集群模式 集群判活
    @Test
    public void isActiveShouldCheckEntireMultipleIfAllActive() {
        given(redisServer1.isActive()).willReturn(Boolean.TRUE);
        given(redisServer2.isActive()).willReturn(Boolean.TRUE);
        given(redisServer3.isActive()).willReturn(Boolean.TRUE);

        List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2, redisServer3);
        redisMultiple = new RedisMultiple(redisServers);

        redisMultiple.isActive();

        redisServers.forEach(redisServer -> verify(redisServer).isActive());
    }
}
