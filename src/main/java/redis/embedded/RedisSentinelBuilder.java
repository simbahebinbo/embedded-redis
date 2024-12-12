package redis.embedded;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.common.CommonConstant;
import redis.embedded.exceptions.RedisBuildingException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class RedisSentinelBuilder {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CONF_FILENAME = "embedded-redis-sentinel";
    private static final String MASTER_MONITOR_LINE = "sentinel monitor %s 127.0.0.1 %d %d";
    private static final String DOWN_AFTER_LINE = "sentinel down-after-milliseconds %s %d";
    private static final String FAILOVER_LINE = "sentinel failover-timeout %s %d";
    private static final String PARALLEL_SYNCS_LINE = "sentinel parallel-syncs %s %d";
    private static final String PORT_LINE = "port %d";

    private File executable;
    private RedisExecProvider redisExecProvider = RedisSentinelExecProvider.defaultProvider();
    private String bind = CommonConstant.ALL_REDIS_HOST;
    private Integer sentinelPort = CommonConstant.DEFAULT_REDIS_STANDALONE_PORT;
    private Integer masterPort = CommonConstant.DEFAULT_REDIS_MASTER_PORT;

    private String masterName = "embedded-master-name";
    private Long downAfterMilliseconds = (Long) 60000L;
    private Long failoverTimeout = (Long) 180000L;
    private Integer parallelSyncs = (Integer) 1;
    private Integer quorumSize = (Integer) 1;
    private String sentinelConf;

    private StringBuilder redisConfigBuilder;

    public RedisSentinelBuilder redisExecProvider(RedisExecProvider redisExecProvider) {
        this.redisExecProvider = redisExecProvider;
        return this;
    }

    public RedisSentinelBuilder bind(String bind) {
        this.bind = bind;
        return this;
    }

    public RedisSentinelBuilder sentinelPort(Integer sentinelPort) {
        this.sentinelPort = sentinelPort;
        return this;
    }

    public RedisSentinelBuilder masterPort(Integer masterPort) {
        this.masterPort = masterPort;
        return this;
    }

    public RedisSentinelBuilder masterName(String masterName) {
        this.masterName = masterName;
        return this;
    }

    public RedisSentinelBuilder quorumSize(Integer quorumSize) {
        this.quorumSize = quorumSize;
        return this;
    }

    public RedisSentinelBuilder downAfterMilliseconds(Long downAfterMilliseconds) {
        this.downAfterMilliseconds = downAfterMilliseconds;
        return this;
    }

    public RedisSentinelBuilder failoverTimeout(Long failoverTimeout) {
        this.failoverTimeout = failoverTimeout;
        return this;
    }

    public RedisSentinelBuilder parallelSyncs(Integer parallelSyncs) {
        this.parallelSyncs = parallelSyncs;
        return this;
    }

    public RedisSentinelBuilder configFile(String redisConf) {
        if (redisConfigBuilder != null) {
            String msg = "Redis configuration is already partially build using setting(String) method!";
            log.warn(msg);
            throw new RedisBuildingException(msg);
        }
        this.sentinelConf = redisConf;
        return this;
    }

    public RedisSentinelBuilder setting(String configLine) {
        if (sentinelConf != null) {
            String msg = "Redis configuration is already set using redis conf file!";
            log.warn(msg);
            throw new RedisBuildingException(msg);
        }

        if (redisConfigBuilder == null) {
            redisConfigBuilder = new StringBuilder();
        }

        redisConfigBuilder.append(configLine);
        redisConfigBuilder.append(LINE_SEPARATOR);
        return this;
    }

    public RedisSentinel build() {
        tryResolveConfAndExec();
        List<String> args = buildCommandArgs();
        return new RedisSentinel(args, sentinelPort);
    }

    private void tryResolveConfAndExec() {
        try {
            if (sentinelConf == null) {
                resolveSentinelConf();
            }
            executable = redisExecProvider.get();
        } catch (Exception e) {
            String msg = "Could not build sentinel instance";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new RedisBuildingException(msg, e);
        }
    }

    public void reset() {
        this.redisConfigBuilder = null;
        this.sentinelConf = null;
    }

    public void addDefaultReplicationGroup() {
        setting(String.format(MASTER_MONITOR_LINE, masterName, masterPort, quorumSize));
        setting(String.format(DOWN_AFTER_LINE, masterName, downAfterMilliseconds));
        setting(String.format(FAILOVER_LINE, masterName, failoverTimeout));
        setting(String.format(PARALLEL_SYNCS_LINE, masterName, parallelSyncs));
    }

    private void resolveSentinelConf() throws IOException {
        if (redisConfigBuilder == null) {
            addDefaultReplicationGroup();
        }
        setting("bind " + bind);
        setting(String.format(PORT_LINE, sentinelPort));

        final String configString = redisConfigBuilder.toString();
        File redisConfigFile =
                File.createTempFile(resolveConfigName(), CommonConstant.CONFIG_FILE_SUFFIX);
        redisConfigFile.deleteOnExit();
        Files.asCharSink(redisConfigFile, StandardCharsets.UTF_8).write(configString);
        sentinelConf = redisConfigFile.getAbsolutePath();
    }

    private String resolveConfigName() {
        return CONF_FILENAME + CommonConstant.SEPARATOR_UNDERSCORE + sentinelPort;
    }

    private List<String> buildCommandArgs() {
        Preconditions.checkNotNull(sentinelConf);

        List<String> args = new LinkedList<>();
        args.add(executable.getAbsolutePath());
        args.add(sentinelConf);

        args.add("--port");
        args.add(Integer.toString(sentinelPort));

        args.add("--loglevel");
        args.add("debug");

        args.add("--daemonize");
        args.add("no");

        args.add("--protected-mode");
        args.add("no");

        return args;
    }
}
