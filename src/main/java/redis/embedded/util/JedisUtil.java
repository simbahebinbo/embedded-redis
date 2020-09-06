package redis.embedded.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import redis.embedded.IRedisServer;
import redis.embedded.RedisBunch;
import redis.embedded.common.CommonConstant;

public class JedisUtil {
  public static Set<String> jedisJedisHosts(IRedisServer redis) {
    final List<Integer> ports = redis.ports();
    return portsToJedisHosts(ports);
  }

  public static Set<String> sentinelJedisHosts(RedisBunch bunch) {
    final List<Integer> ports = bunch.sentinelPorts();
    return portsToJedisHosts(ports);
  }

  public static Set<String> portsToJedisHosts(List<Integer> ports) {
    Set<String> hosts = new HashSet<>();
    for (Integer port : ports) {
      hosts.add(CommonConstant.DEFAULT_REDIS_HOST + CommonConstant.SEPARATOR_COLON + port);
    }
    return hosts;
  }
}
