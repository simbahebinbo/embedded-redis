package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RedisBunch implements IRedisServer {
    private final List<RedisSentinel> redisSentinels = new LinkedList<>();
    private final List<RedisServer> redisServers = new LinkedList<>();

    RedisBunch(List<RedisSentinel> redisSentinels, List<RedisServer> redisServers) {
        this.redisSentinels.addAll(redisSentinels);
        this.redisServers.addAll(redisServers);
    }

    public static RedisBunchBuilder builder() {
        return new RedisBunchBuilder();
    }

    @Override
    public boolean isActive() {
        for (RedisSentinel redisSentinel : redisSentinels) {
            if (!redisSentinel.isActive()) {
                return false;
            }
        }
        for (RedisServer redisServer : redisServers) {
            if (!redisServer.isActive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void start() throws EmbeddedRedisException {
        for (RedisSentinel redisSentinel : redisSentinels) {
            redisSentinel.start();
        }
        for (RedisServer redisServer : redisServers) {
            redisServer.start();
        }
    }

    @Override
    public void stop() throws EmbeddedRedisException {
        for (RedisSentinel redisSentinel : redisSentinels) {
            redisSentinel.stop();
        }
        for (RedisServer redisServer : redisServers) {
            redisServer.stop();
        }
    }

    @Override
    public Set<Integer> ports() {
        Set<Integer> ports = new LinkedHashSet<>();
        ports.addAll(sentinelPorts());
        ports.addAll(serverPorts());
        return ports;
    }

    public List<RedisSentinel> sentinels() {
        return Lists.newLinkedList(redisSentinels);
    }

    public Set<Integer> sentinelPorts() {
        Set<Integer> ports = new LinkedHashSet<>();
        for (RedisSentinel redisSentinel : redisSentinels) {
            ports.addAll(redisSentinel.ports());
        }
        return ports;
    }

    public List<RedisServer> servers() {
        return Lists.newLinkedList(redisServers);
    }

    public Set<Integer> serverPorts() {
        Set<Integer> ports = new LinkedHashSet<>();
        for (RedisServer redisServer : redisServers) {
            ports.addAll(redisServer.ports());
        }
        return ports;
    }
}
