package redis.embedded;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.List;
import java.util.Set;

@Slf4j
abstract class AbstractRedisServerInstance extends AbstractRedisInstance implements IRedisServer {

    private final Set<Integer> ports = Sets.newHashSet();

    private final Set<Integer> sentinelPorts = Sets.newHashSet();
    private final Set<Integer> masterPorts = Sets.newHashSet();

    AbstractRedisServerInstance(List<String> args) {
        super(args);
    }

    protected AbstractRedisServerInstance(int port) {
        super();
        this.ports.add(port);
    }

    AbstractRedisServerInstance(List<String> args, int port) {
        super(args);
        this.ports.add(port);
    }

    AbstractRedisServerInstance(int sentinelPort, int masterPort) {
        super();
        this.ports.add(sentinelPort);
        this.ports.add(masterPort);
        this.sentinelPorts.add(sentinelPort);
        this.masterPorts.add(masterPort);
    }

    AbstractRedisServerInstance(List<String> args, int sentinelPort, int masterPort) {
        super(args);
        this.ports.add(sentinelPort);
        this.ports.add(masterPort);
        this.sentinelPorts.add(sentinelPort);
        this.masterPorts.add(masterPort);
    }

    @Override
    public synchronized void start() throws EmbeddedRedisException {
        doStart();
    }

    @Override
    public synchronized void stop() throws EmbeddedRedisException {
        doStop();
    }

    protected abstract String redisServerReadyPattern();

    @Override
    public String redisInstanceReadyPattern() {
        return redisServerReadyPattern();
    }

    @Override
    public Set<Integer> ports() {
        return ports;
    }


    public Set<Integer> sentinelPorts() {
        return sentinelPorts;
    }

    public Set<Integer> masterPorts() {
        return masterPorts;
    }
}
