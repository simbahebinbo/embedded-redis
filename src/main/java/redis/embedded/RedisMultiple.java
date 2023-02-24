package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class RedisMultiple implements IRedisServer {
    private final List<RedisServer> redisServers = new LinkedList<>();

    RedisMultiple(List<RedisServer> redisServers) {
        this.redisServers.addAll(redisServers);
    }

    public static RedisMultipleBuilder builder() {
        return new RedisMultipleBuilder();
    }

    @Override
    public boolean isActive() {
        for (RedisServer redisServer : redisServers) {
            if (!redisServer.isActive()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void start() throws EmbeddedRedisException {
        for (RedisServer redisServer : redisServers) {
            redisServer.start();
        }
    }

    @Override
    public void stop() throws EmbeddedRedisException {
        for (RedisServer redisServer : redisServers) {
            redisServer.stop();
        }
    }

    @Override
    public Set<Integer> ports() {
        Set<Integer> ports = new LinkedHashSet<>(serverPorts());
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

