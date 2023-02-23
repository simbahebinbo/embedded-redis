package redis.embedded.util;

import redis.embedded.RedisBunch;
import redis.embedded.RedisCluster;
import redis.embedded.RedisSentinel;
import redis.embedded.RedisServer;
import redis.embedded.common.CommonConstant;

import java.util.HashSet;
import java.util.Set;

public class JedisUtil {
    public static Set<String> serverJedisHosts(RedisServer server) {
        final Set<Integer> ports = server.ports();
        return portsToJedisHosts(ports);
    }

    public static Set<String> bunchJedisHosts(RedisBunch bunch) {
        final Set<Integer> ports = bunch.ports();
        return portsToJedisHosts(ports);
    }

    public static Set<String> sentinelJedisHosts(RedisBunch bunch) {
        final Set<Integer> ports = bunch.sentinelPorts();
        return portsToJedisHosts(ports);
    }

    public static Set<String> serverJedisHosts(RedisBunch bunch) {
        final Set<Integer> ports = bunch.serverPorts();
        return portsToJedisHosts(ports);
    }

    public static Set<String> sentinelJedisHosts(RedisSentinel sentinel) {
        final Set<Integer> ports = sentinel.sentinelPorts();
        return portsToJedisHosts(ports);
    }

    public static Set<String> masterJedisHosts(RedisSentinel sentinel) {
        final Set<Integer> ports = sentinel.masterPorts();
        return portsToJedisHosts(ports);
    }

    public static Set<String> clusterJedisHosts(RedisCluster cluster) {
        final Set<Integer> ports = cluster.ports();
        return portsToJedisHosts(ports);
    }

    public static Set<String> portsToJedisHosts(Set<Integer> ports) {
        Set<String> hosts = new HashSet<>();
        for (Integer port : ports) {
            hosts.add(CommonConstant.DEFAULT_REDIS_HOST + CommonConstant.SEPARATOR_COLON + port);
        }
        return hosts;
    }
}
