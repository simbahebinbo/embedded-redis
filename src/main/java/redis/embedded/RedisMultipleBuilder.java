package redis.embedded;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class RedisMultipleBuilder {
    private final Set<Integer> serverPorts = new HashSet<>();

    private RedisServerBuilder serverBuilder = new RedisServerBuilder();

    public RedisMultipleBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisMultipleBuilder serverPorts(Set<Integer> ports) {
        this.serverPorts.addAll(ports);
        return this;
    }

    public RedisMultiple build() {
        final List<RedisServer> redisServers = buildServers();
        return new RedisMultiple(redisServers);
    }

    private List<RedisServer> buildServers() {
        List<RedisServer> servers = new ArrayList<>();
        for (Integer serverPort : serverPorts) {
            serverBuilder.reset();
            serverBuilder.port(serverPort);
            final RedisServer server = serverBuilder.build();
            servers.add(server);
        }
        return servers;
    }
}
