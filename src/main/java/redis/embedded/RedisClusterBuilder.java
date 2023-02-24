package redis.embedded;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class RedisClusterBuilder {
    private final Set<Integer> nodePorts = new LinkedHashSet<>();
    private RedisServerBuilder serverBuilder = new RedisServerBuilder();
    private RedisClientBuilder clientBuilder = new RedisClientBuilder();

    private Integer clusterReplicas = 0;


    public RedisClusterBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisClusterBuilder withClientBuilder(RedisClientBuilder clientBuilder) {
        this.clientBuilder = clientBuilder;
        return this;
    }

    public RedisClusterBuilder nodePorts(Set<Integer> ports) {
        this.nodePorts.addAll(ports);
        return this;
    }

    public RedisClusterBuilder clusterReplicas(Integer clusterReplicas) {
        this.clusterReplicas = clusterReplicas;
        return this;
    }

    public RedisCluster build() {
        final List<RedisServer> servers = buildServers();
        final RedisClient client = buildClient();
        return new RedisCluster(servers, client);
    }

    private List<RedisServer> buildServers() {
        List<RedisServer> servers = new ArrayList<>();
        for (Integer nodePort : nodePorts) {
            serverBuilder.reset();
            serverBuilder.port(nodePort);
            serverBuilder.clusterEnable(true);
            final RedisServer server = serverBuilder.build();
            servers.add(server);
        }
        return servers;
    }

    private RedisClient buildClient() {
        clientBuilder.reset();
        clientBuilder.ports(nodePorts);
        clientBuilder.clusterReplicas(clusterReplicas);
        RedisClient client = clientBuilder.build();
        return client;
    }
}
