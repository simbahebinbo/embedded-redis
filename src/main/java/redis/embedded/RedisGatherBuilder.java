package redis.embedded;

import lombok.NoArgsConstructor;
import redis.embedded.common.CommonConstant;
import redis.embedded.ports.EphemeralPortProvider;
import redis.embedded.ports.PredefinedPortProvider;
import redis.embedded.ports.SequencePortProvider;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class RedisGatherBuilder {
    private final List<ReplicationGroup> groups = new LinkedList<>();
    private RedisServerBuilder serverBuilder = new RedisServerBuilder();

    private PortProvider replicationGroupPortProvider =
            new SequencePortProvider(CommonConstant.DEFAULT_REDIS_PORT);

    public RedisGatherBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisGatherBuilder serverPorts(Set<Integer> ports) {
        this.replicationGroupPortProvider = new PredefinedPortProvider(ports);
        return this;
    }

    public RedisGatherBuilder serverPorts(Integer masterPort, Set<Integer> slavePorts) {
        LinkedHashSet<Integer> ports = new LinkedHashSet<>();
        ports.add(masterPort);
        ports.addAll(slavePorts);
        this.replicationGroupPortProvider = new PredefinedPortProvider(ports);
        return this;
    }

    public RedisGatherBuilder ephemeralServers() {
        this.replicationGroupPortProvider = new EphemeralPortProvider();
        return this;
    }

    public RedisGatherBuilder ephemeral() {
        ephemeralServers();
        return this;
    }

    public RedisGatherBuilder replicationGroup(int slaveCount) {
        this.groups.add(
                new ReplicationGroup(slaveCount, this.replicationGroupPortProvider));
        return this;
    }

    public RedisGather build() {
        final List<RedisServer> redisServers = buildServers();
        return new RedisGather(redisServers);
    }

    private List<RedisServer> buildServers() {
        List<RedisServer> redisServers = new LinkedList<>();
        for (ReplicationGroup group : groups) {
            redisServers.add(buildMaster(group));
            buildSlaves(redisServers, group);
        }
        return redisServers;
    }

    private void buildSlaves(List<RedisServer> redisServers, ReplicationGroup group) {
        for (Integer slavePort : group.slavePorts) {
            serverBuilder.reset();
            serverBuilder.port(slavePort);
            serverBuilder.replicaOf(group.masterPort);
            final RedisServer slave = serverBuilder.build();
            redisServers.add(slave);
        }
    }

    private RedisServer buildMaster(ReplicationGroup group) {
        serverBuilder.reset();
        return serverBuilder.port(group.masterPort).build();
    }

    private static class ReplicationGroup {
        private final int masterPort;
        private final Set<Integer> slavePorts = new LinkedHashSet<>();

        private ReplicationGroup(int slaveCount, PortProvider portProvider) {
            masterPort = portProvider.next();
            while (slaveCount-- > 0) {
                slavePorts.add(portProvider.next());
            }
        }
    }
}
