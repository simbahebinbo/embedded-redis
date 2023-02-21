package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public List<Integer> ports() {
        return new ArrayList<>(serverPorts());
    }

    public List<RedisServer> servers() {
        return Lists.newLinkedList(redisServers);
    }

    public List<Integer> serverPorts() {
        List<Integer> ports = new ArrayList<>();
        for (RedisServer redisServer : redisServers) {
            ports.addAll(redisServer.ports());
        }
        return ports;
    }
}
