package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.ArrayList;
import java.util.HashSet;
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
        Set<Integer> ports = new HashSet<>();
        ports.addAll(sentinelPorts());
        ports.addAll(serverPorts());
        return ports;
    }

    @Override
    public Set<Integer> tlsPorts() {
        Set<Integer> tlsPorts = new HashSet<>();
        tlsPorts.addAll(sentinelTlsPorts());
        tlsPorts.addAll(serverTlsPorts());
        return tlsPorts;
    }

    public List<RedisSentinel> sentinels() {
        return Lists.newLinkedList(redisSentinels);
    }

    public Set<Integer> sentinelPorts() {
        Set<Integer> ports = new HashSet<>();
        for (RedisSentinel redisSentinel : redisSentinels) {
            ports.addAll(redisSentinel.ports());
        }
        return ports;
    }

    public Set<Integer> sentinelTlsPorts() {
        Set<Integer> tlsPorts = new HashSet<>();
        for (RedisSentinel redisSentinel : redisSentinels) {
            tlsPorts.addAll(redisSentinel.tlsPorts());
        }
        return tlsPorts;
    }


    public List<RedisServer> servers() {
        return Lists.newLinkedList(redisServers);
    }

    public Set<Integer> serverPorts() {
        Set<Integer> ports = new HashSet<>();
        for (RedisServer redisServer : redisServers) {
            ports.addAll(redisServer.ports());
        }
        return ports;
    }

    public List<Integer> serverTlsPorts() {
        List<Integer> tlsPorts = new ArrayList<>();
        for (RedisServer redisServer : redisServers) {
            tlsPorts.addAll(redisServer.tlsPorts());
        }
        return tlsPorts;
    }
}
