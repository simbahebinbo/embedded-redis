package redis.embedded.util;

import redis.embedded.Redis;
import redis.embedded.RedisCluster;
import redis.embedded.common.CommonConstant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JedisUtil {
  public static Set<String> jedisHosts(Redis redis) {
    final List<Integer> ports = redis.ports();
    return portsToJedisHosts(ports);
  }

  public static Set<String> sentinelHosts(RedisCluster cluster) {
    final List<Integer> ports = cluster.sentinelPorts();
    return portsToJedisHosts(ports);
  }

  public static Set<String> portsToJedisHosts(List<Integer> ports) {
    Set<String> hosts = new HashSet<>();
    for (Integer p : ports) {
      hosts.add(CommonConstant.DEFAULT_REDIS_HOST + CommonConstant.SEPARATOR_COLON + p);
    }
    return hosts;
  }
}
