package redis.embedded;

import com.google.common.collect.Lists;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class RedisGather implements IRedisServer {
    private final List<RedisServer> redisServers = new LinkedList<>();

    RedisGather(List<RedisServer> redisServers) {
        this.redisServers.addAll(redisServers);
    }

    public static RedisGatherBuilder builder() {
        return new RedisGatherBuilder();
    }

    @Override
    public boolean isActive() {
        return redisServers.stream().allMatch(AbstractRedisInstance::isActive);
    }

    @Override
    public void start() throws EmbeddedRedisException {
        redisServers.stream().parallel().forEach(AbstractRedisServerInstance::start);
    }

    @Override
    public void stop() throws EmbeddedRedisException {
        redisServers.stream().parallel().forEach(AbstractRedisServerInstance::stop);
    }

    @Override
    public Set<Integer> ports() {
        Set<Integer> ports = new LinkedHashSet<>(serverPorts());
        return ports;
    }

    public List<RedisServer> servers() {
        LinkedList<RedisServer> servers = Lists.newLinkedList(redisServers);
        return servers;
    }

    public RedisServer masterServer() {
        LinkedList<RedisServer> servers = Lists.newLinkedList(redisServers);
        RedisServer masterServer = servers.peek();
        return masterServer;
    }

    public List<RedisServer> slaveServers() {
        LinkedList<RedisServer> servers = Lists.newLinkedList(redisServers);
        RedisServer masterServer = servers.pop();
        return servers;
    }


    public Set<Integer> serverPorts() {
        LinkedHashSet<Integer> ports = new LinkedHashSet<>();
        redisServers.forEach(redisServer -> ports.addAll(redisServer.ports()));
        return ports;
    }

    public Integer masterPort() {
        LinkedHashSet<Integer> ports = new LinkedHashSet<>(serverPorts());
        Integer masterPort = 0;
        Optional<Integer> portOptional = ports.stream().findFirst();
        if (portOptional.isPresent()) {
            masterPort = portOptional.get();
        }
        return masterPort;
    }

    public Set<Integer> slavePorts() {
        LinkedHashSet<Integer> ports = new LinkedHashSet<>(serverPorts());
        LinkedHashSet<Integer> slavePorts = ports.stream().skip(1).collect(Collectors.toCollection(LinkedHashSet::new));
        return slavePorts;
    }
}
