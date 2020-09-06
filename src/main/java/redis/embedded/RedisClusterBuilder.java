package redis.embedded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import redis.embedded.common.CommonConstant;
import redis.embedded.ports.EphemeralPortProvider;
import redis.embedded.ports.PredefinedPortProvider;
import redis.embedded.ports.SequencePortProvider;

public class RedisClusterBuilder {
  private RedisServerBuilder serverBuilder = new RedisServerBuilder();
  private PortProvider replicationGroupPortProvider =
      new SequencePortProvider(CommonConstant.DEFAULT_REDIS_PORT);
  private final List<ReplicationGroup> groups = new LinkedList<>();

  public RedisClusterBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
    this.serverBuilder = serverBuilder;
    return this;
  }

  public RedisClusterBuilder serverPorts(Collection<Integer> ports) {
    this.replicationGroupPortProvider = new PredefinedPortProvider(ports);
    return this;
  }

  public RedisClusterBuilder ephemeralServers() {
    this.replicationGroupPortProvider = new EphemeralPortProvider();
    return this;
  }

  public RedisClusterBuilder ephemeral() {
    ephemeralServers();
    return this;
  }

  public RedisClusterBuilder replicationGroup(String masterHost, int masterPort, int slaveCount) {
    this.groups.add(
        new ReplicationGroup(
            masterHost, masterPort, slaveCount, this.replicationGroupPortProvider));
    return this;
  }

  public RedisCluster build() {
    final List<RedisServer> servers = buildServers();
    return new RedisCluster(servers);
  }

  private List<RedisServer> buildServers() {
    List<RedisServer> servers = new ArrayList<>();
    for (ReplicationGroup group : groups) {
      servers.add(buildMaster(group));
      buildSlaves(servers, group);
    }
    return servers;
  }

  private void buildSlaves(List<RedisServer> servers, ReplicationGroup group) {
    for (Integer slavePort : group.slavePorts) {
      serverBuilder.reset();
      serverBuilder.port(slavePort);
      serverBuilder.slaveOf(CommonConstant.DEFAULT_REDIS_HOST, group.masterPort);
      final RedisServer slave = serverBuilder.build();
      servers.add(slave);
    }
  }

  private RedisServer buildMaster(ReplicationGroup group) {
    serverBuilder.reset();
    return serverBuilder.port(group.masterPort).build();
  }

  private static class ReplicationGroup {
    private final String masterHost;
    private final int masterPort;
    private final List<Integer> slavePorts = new LinkedList<>();

    private ReplicationGroup(
        String masterHost, int masterPort, int slaveCount, PortProvider portProvider) {
      this.masterHost = masterHost;
      this.masterPort = masterPort;
      while (slaveCount-- > 0) {
        slavePorts.add(portProvider.next());
      }
    }
  }
}
