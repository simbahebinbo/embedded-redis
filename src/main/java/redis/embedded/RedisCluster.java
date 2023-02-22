package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RedisCluster implements IRedisServer {
    private final List<RedisServer> redisServers = new LinkedList<>();

    private final RedisClient redisClient;

    RedisCluster(List<RedisServer> redisServers, RedisClient redisClient) {
        this.redisServers.addAll(redisServers);
        this.redisClient = redisClient;
    }

    public static RedisClusterBuilder builder() {
        return new RedisClusterBuilder();
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
        redisClient.run();
    }

    @Override
    public void stop() throws EmbeddedRedisException {
        for (RedisServer redisServer : redisServers) {
            redisServer.stop();
        }
    }

    @Override
    public Set<Integer> ports() {
        return new HashSet<>(serverPorts());
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
}
