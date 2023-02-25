package redis.embedded;

import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class RedisMultipleBuilder {
    private final Set<Integer> masterPorts = new LinkedHashSet<>();

    private RedisServerBuilder serverBuilder = new RedisServerBuilder();

    public RedisMultipleBuilder withServerBuilder(RedisServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
        return this;
    }

    public RedisMultipleBuilder masterPorts(Set<Integer> ports) {
        this.masterPorts.addAll(ports);
        return this;
    }

    public RedisMultiple build() {
        final List<RedisServer> redisServers = buildMasters();
        return new RedisMultiple(redisServers);
    }

    private List<RedisServer> buildMasters() {
        List<RedisServer> servers = new LinkedList<>();
        masterPorts.forEach(masterPort -> {
            serverBuilder.reset();
            serverBuilder.port(masterPort);
            final RedisServer server = serverBuilder.build();
            servers.add(server);
        });
        return servers;
    }
}
