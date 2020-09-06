package redis.embedded;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  public void stopShouldStopEntireBunch() {

    List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
    List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
    redisBunch = new RedisBunch(redisSentinels, redisServers);

    redisBunch.stop();

    for (RedisSentinel redisSentinel : redisSentinels) {
      verify(redisSentinel).stop();
    }
    for (RedisServer redisServer : redisServers) {
      verify(redisServer).stop();
    }
  }

  @Test
  public void startShouldStartEntireBunch() {
    List<RedisSentinel> redisSentinels = Arrays.asList(redisSentinel1, redisSentinel2);
    List<RedisServer> redisServers = Arrays.asList(redisServer1, redisServer2);
    redisBunch = new RedisBunch(redisSentinels, redisServers);

    redisBunch.start();

    for (RedisSentinel redisSentinel : redisSentinels) {
      verify(redisSentinel).start();
    }
    for (RedisServer redisServer : redisServers) {
      verify(redisServer).start();
    }
  }

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

    for (RedisSentinel redisSentinel : redisSentinels) {
      verify(redisSentinel).isActive();
    }
    for (RedisServer redisServer : redisServers) {
      verify(redisServer).isActive();
    }
  }
}
