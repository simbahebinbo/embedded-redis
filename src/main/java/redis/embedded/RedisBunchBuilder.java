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
public class RedisBunchBuilder {
    private final List<ReplicationGroup> groups = new LinkedList<>();
    private RedisSentinelBuilder sentinelBuilder = new RedisSentinelBuilder();
    private RedisServerBuilder serverBuilder = new RedisServerBuilder();
    private Integer sentinelCount = (Integer) 1;
    private Integer quorumSize = (Integer) 1;
    private PortProvider sentinelPortProvider =
            new SequencePortProvider(CommonConstant.DEFAULT_REDIS_SENTINEL_PORT);
    private PortProvider replicationGroupPortProvider =
            new SequencePortProvider(CommonConstant.DEFAULT_REDIS_PORT);

    public RedisBunchBuilder withSentinelBuilder(RedisSentinelBuilder sentinelBuilder) {
        this.sentinelBuilder = sentinelBuilder;
        return this;
    }

    public RedisBunchBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisBunchBuilder sentinelPorts(Set<Integer> ports) {
        this.sentinelPortProvider = new PredefinedPortProvider(ports);
        this.sentinelCount = (Integer) ports.size();
        return this;
    }

    public RedisBunchBuilder serverPorts(Set<Integer> ports) {
        this.replicationGroupPortProvider = new PredefinedPortProvider(ports);
        return this;
    }

    public RedisBunchBuilder ephemeralSentinels() {
        this.sentinelPortProvider = new EphemeralPortProvider();
        return this;
    }

    public RedisBunchBuilder ephemeralServers() {
        this.replicationGroupPortProvider = new EphemeralPortProvider();
        return this;
    }

    public RedisBunchBuilder ephemeral() {
        ephemeralSentinels();
        ephemeralServers();
        return this;
    }

    public RedisBunchBuilder sentinelCount(Integer sentinelCount) {
        this.sentinelCount = sentinelCount;
        return this;
    }

    public RedisBunchBuilder sentinelStartingPort(Integer startingPort) {
        this.sentinelPortProvider = new SequencePortProvider(startingPort);
        return this;
    }

    public RedisBunchBuilder quorumSize(Integer quorumSize) {
        this.quorumSize = quorumSize;
        return this;
    }

    public RedisBunchBuilder replicationGroup(String masterName, Integer slaveCount) {
        this.groups.add(
                new ReplicationGroup(masterName, slaveCount, this.replicationGroupPortProvider));
        return this;
    }

    public RedisBunch build() {
        final List<RedisSentinel> redisSentinels = buildSentinels();
        final List<RedisServer> redisServers = buildServers();
        return new RedisBunch(redisSentinels, redisServers);
    }

    private List<RedisServer> buildServers() {
        List<RedisServer> redisServers = new LinkedList<>();
        groups.forEach(group -> {
            redisServers.add(buildMaster(group));
            redisServers.addAll(buildSlaves(group));
        });
        return redisServers;
    }

    private List<RedisServer> buildSlaves(ReplicationGroup group) {
        List<RedisServer> redisServers = new LinkedList<>();
        group.slavePorts.forEach(slavePort -> {
            serverBuilder.reset();
            serverBuilder.port(slavePort);
            serverBuilder.replicaOf(group.masterPort);
            final RedisServer slave = serverBuilder.build();
            redisServers.add(slave);
        });
        return redisServers;
    }

    private RedisServer buildMaster(ReplicationGroup group) {
        serverBuilder.reset();
        final RedisServer master = serverBuilder.port(group.masterPort).build();
        return master;
    }

    private List<RedisSentinel> buildSentinels() {
        Integer toBuild = this.sentinelCount;
        final List<RedisSentinel> redisSentinels = new LinkedList<>();
        while (toBuild-- > 0) {
            redisSentinels.add(buildSentinel());
        }
        return redisSentinels;
    }

    private RedisSentinel buildSentinel() {
        sentinelBuilder.reset();
        sentinelBuilder.sentinelPort(nextSentinelPort());
        groups.forEach(group -> {
            sentinelBuilder.masterName(group.masterName);
            sentinelBuilder.masterPort(group.masterPort);
            sentinelBuilder.quorumSize(quorumSize);
            sentinelBuilder.addDefaultReplicationGroup();
        });
        return sentinelBuilder.build();
    }

    private Integer nextSentinelPort() {
        return sentinelPortProvider.next();
    }

    private static class ReplicationGroup {
        private final String masterName;
        private final Integer masterPort;
        private final Set<Integer> slavePorts = new LinkedHashSet<>();

        private ReplicationGroup(String masterName, Integer slaveCount, PortProvider portProvider) {
            this.masterName = masterName;
            masterPort = portProvider.next();
            while (slaveCount-- > 0) {
                slavePorts.add(portProvider.next());
            }
        }
    }
}
