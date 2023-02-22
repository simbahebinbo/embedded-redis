package redis.embedded;

import redis.embedded.common.CommonConstant;
import redis.embedded.ports.EphemeralPortProvider;
import redis.embedded.ports.PredefinedPortProvider;
import redis.embedded.ports.SequencePortProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RedisBunchBuilder {
    private final List<ReplicationGroup> groups = new LinkedList<>();
    private RedisSentinelBuilder sentinelBuilder = new RedisSentinelBuilder();
    private RedisServerBuilder serverBuilder = new RedisServerBuilder();
    private int sentinelCount = 1;
    private int quorumSize = 1;
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

    public RedisBunchBuilder sentinelPorts(Collection<Integer> ports) {
        this.sentinelPortProvider = new PredefinedPortProvider(ports);
        this.sentinelCount = ports.size();
        return this;
    }

    public RedisBunchBuilder serverPorts(Collection<Integer> ports) {
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

    public RedisBunchBuilder sentinelCount(int sentinelCount) {
        this.sentinelCount = sentinelCount;
        return this;
    }

    public RedisBunchBuilder sentinelStartingPort(int startingPort) {
        this.sentinelPortProvider = new SequencePortProvider(startingPort);
        return this;
    }

    public RedisBunchBuilder quorumSize(int quorumSize) {
        this.quorumSize = quorumSize;
        return this;
    }

    public RedisBunchBuilder replicationGroup(String masterName, int slaveCount) {
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
        List<RedisServer> redisServers = new ArrayList<>();
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
            serverBuilder.slaveOf(group.masterPort);
            final RedisServer slave = serverBuilder.build();
            redisServers.add(slave);
        }
    }

    private RedisServer buildMaster(ReplicationGroup group) {
        serverBuilder.reset();
        return serverBuilder.port(group.masterPort).build();
    }

    private List<RedisSentinel> buildSentinels() {
        int toBuild = this.sentinelCount;
        final List<RedisSentinel> redisSentinels = new LinkedList<>();
        while (toBuild-- > 0) {
            redisSentinels.add(buildSentinel());
        }
        return redisSentinels;
    }

    private RedisSentinel buildSentinel() {
        sentinelBuilder.reset();
        sentinelBuilder.sentinelPort(nextSentinelPort());
        for (ReplicationGroup group : groups) {
            sentinelBuilder.masterName(group.masterName);
            sentinelBuilder.masterPort(group.masterPort);
            sentinelBuilder.quorumSize(quorumSize);
            sentinelBuilder.addDefaultReplicationGroup();
        }
        return sentinelBuilder.build();
    }

    private int nextSentinelPort() {
        return sentinelPortProvider.next();
    }

    private static class ReplicationGroup {
        private final String masterName;
        private final int masterPort;
        private final List<Integer> slavePorts = new LinkedList<>();

        private ReplicationGroup(String masterName, int slaveCount, PortProvider portProvider) {
            this.masterName = masterName;
            masterPort = portProvider.next();
            while (slaveCount-- > 0) {
                slavePorts.add(portProvider.next());
            }
        }
    }
}
