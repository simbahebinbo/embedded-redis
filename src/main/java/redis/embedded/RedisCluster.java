package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.LinkedHashSet;
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
        return redisServers.stream().allMatch(AbstractRedisInstance::isActive);
    }

    @Override
    public void start() throws EmbeddedRedisException {
        redisServers.stream().parallel().forEach(AbstractRedisServerInstance::start);
        redisClient.run();
    }

    @Override
    public void stop() throws EmbeddedRedisException {
        redisServers.stream().parallel().forEach(AbstractRedisServerInstance::stop);
    }

    @Override
    public Set<Integer> ports() {
        return new LinkedHashSet<>(nodePorts());
    }

    public List<RedisServer> nodes() {
        return Lists.newLinkedList(redisServers);
    }

    public Set<Integer> nodePorts() {
        Set<Integer> ports = new LinkedHashSet<>();
        redisServers.forEach(redisServer -> ports.addAll(redisServer.ports()));
        return ports;
    }
}
