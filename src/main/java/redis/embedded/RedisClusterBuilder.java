package redis.embedded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RedisClusterBuilder {
    private Collection<Integer> serverPorts = new LinkedList<>();
    private RedisServerBuilder serverBuilder = new RedisServerBuilder();

    public RedisClusterBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisClusterBuilder serverPorts(Collection<Integer> ports) {
        this.serverPorts = ports;
        return this;
    }

    public RedisCluster build() {
        final List<RedisServer> servers = buildServers();
        return new RedisCluster(servers);
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
}
