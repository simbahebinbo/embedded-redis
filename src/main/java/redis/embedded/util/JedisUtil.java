package redis.embedded.util;

import redis.embedded.RedisBunch;
import redis.embedded.RedisCluster;
import redis.embedded.RedisGather;
import redis.embedded.RedisMultiple;
import redis.embedded.RedisSentinel;
import redis.embedded.RedisServer;
import redis.embedded.common.CommonConstant;

import java.util.LinkedHashSet;
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

    public static Set<String> multipleJedisHosts(RedisMultiple multiple) {
        final Set<Integer> ports = multiple.ports();
        return portsToJedisHosts(ports);
    }

    public static Set<String> gatherJedisHosts(RedisGather gather) {
        final Set<Integer> ports = gather.ports();
        return portsToJedisHosts(ports);
    }

    public static Set<String> masterJedisHosts(RedisGather gather) {
        final Integer port = gather.masterPort();
        return portsToJedisHost(port);
    }

    public static Set<String> slaveJedisHosts(RedisGather gather) {
        final Set<Integer> ports = gather.slavePorts();
        return portsToJedisHosts(ports);
    }

    public static Set<String> portsToJedisHosts(Set<Integer> ports) {
        Set<String> hosts = new LinkedHashSet<>();
        ports.forEach(port -> hosts.add(CommonConstant.DEFAULT_REDIS_HOST + CommonConstant.SEPARATOR_COLON + port));
        return hosts;
    }

    public static Set<String> portsToJedisHost(Integer port) {
        Set<String> hosts = new LinkedHashSet<>();
        hosts.add(CommonConstant.DEFAULT_REDIS_HOST + CommonConstant.SEPARATOR_COLON + port);
        return hosts;
    }
}
