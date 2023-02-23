package redis.embedded;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class RedisClusterBuilder {
    private final Collection<Integer> serverPorts = new LinkedList<>();
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

    public RedisClusterBuilder serverPorts(Collection<Integer> ports) {
        this.serverPorts.addAll(ports);
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
        for (Integer serverPort : serverPorts) {
            serverBuilder.reset();
            serverBuilder.port(serverPort);
            serverBuilder.clusterEnable(true);
            final RedisServer server = serverBuilder.build();
            servers.add(server);
        }
        return servers;
    }

    private RedisClient buildClient() {
        clientBuilder.reset();
        clientBuilder.ports(serverPorts);
        clientBuilder.clusterReplicas(clusterReplicas);
        RedisClient client = clientBuilder.build();
        return client;
    }
}
