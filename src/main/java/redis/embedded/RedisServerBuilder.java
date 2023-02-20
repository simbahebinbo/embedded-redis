package redis.embedded;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.embedded.common.CommonConstant;
import redis.embedded.exceptions.RedisBuildingException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RedisServerBuilder {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CONF_FILENAME = "embedded-redis-server";
    //主从模式参数
    private Boolean masterEnable = false;
    private Boolean slaveEnable = false;
    private File executable;
    private RedisExecProvider redisExecProvider = RedisServerExecProvider.defaultProvider();
    private String bind = CommonConstant.ALL_REDIS_HOST;
    private int port = CommonConstant.DEFAULT_REDIS_STANDALONE_PORT;
    private InetSocketAddress replicaOf;
    //集群模式参数
    private Boolean clusterEnable = false;
    private InetSocketAddress slaveOf;
    //哨兵模式参数
    private Boolean sentinelEnable = false;
    private String redisConf;

    private StringBuilder redisConfigBuilder;

    public RedisServerBuilder redisExecProvider(RedisExecProvider redisExecProvider) {
        this.redisExecProvider = redisExecProvider;
        return this;
    }

    public RedisServerBuilder bind(String bind) {
        this.bind = bind;
        return this;
    }

    public RedisServerBuilder port(int port) {
        this.port = port;
        return this;
    }

    public RedisServerBuilder clusterEnable(boolean enable) {
        this.clusterEnable = enable;
        return this;
    }

    public RedisServerBuilder sentinelEnable(boolean enable) {
        this.sentinelEnable = enable;
        return this;
    }

    public RedisServerBuilder masterEnable(boolean enable) {
        this.masterEnable = enable;
        return this;
    }

    public RedisServerBuilder slaveEnable(boolean enable) {
        this.slaveEnable = enable;
        return this;
    }


    public RedisServerBuilder slaveOf(String hostname, int port) {
        this.slaveOf = new InetSocketAddress(hostname, port);
        return this;
    }

    public RedisServerBuilder slaveOf(InetSocketAddress slaveOf) {
        this.slaveOf = slaveOf;
        return this;
    }

    public RedisServerBuilder replicaOf(String hostname, int port) {
        this.replicaOf = new InetSocketAddress(hostname, port);
        return this;
    }

    public RedisServerBuilder replicaOf(InetSocketAddress replicaOf) {
        this.replicaOf = replicaOf;
        return this;
    }


    public RedisServerBuilder configFile(String redisConf) {
        if (redisConfigBuilder != null) {
            log.warn("Redis configuration is already partially build using setting(String) method!");
            throw new RedisBuildingException(
                    "Redis configuration is already partially build using setting(String) method!");
        }
        this.redisConf = redisConf;
        return this;
    }

    public RedisServerBuilder setting(String configLine) {
        if (redisConf != null) {
            log.warn("Redis configuration is already set using redis conf file!");
            throw new RedisBuildingException("Redis configuration is already set using redis conf file!");
        }

        if (redisConfigBuilder == null) {
            redisConfigBuilder = new StringBuilder();
        }

        redisConfigBuilder.append(configLine);
        redisConfigBuilder.append(LINE_SEPARATOR);
        return this;
    }

    public RedisServer build() {
        setting("bind " + bind);
        tryResolveConfAndExec();
        List<String> args = buildCommandArgs();
        return new RedisServer(args, port);
    }

    public void reset() {
        this.executable = null;
        this.redisConfigBuilder = null;
        this.replicaOf = null;
        this.redisConf = null;
        this.clusterEnable = false;
        this.slaveOf = null;
        this.sentinelEnable = false;
        this.masterEnable = false;
        this.slaveEnable = false;
    }

    private void tryResolveConfAndExec() {
        try {
            resolveConfAndExec();
        } catch (IOException e) {
            log.warn("Could not build server instance. exception: {}", e.getMessage(), e);
            throw new RedisBuildingException("Could not build server instance", e);
        }
    }

    private void resolveConfAndExec() throws IOException {
        if ((redisConf == null) && (redisConfigBuilder != null)) {
            File redisConfigFile =
                    File.createTempFile(resolveConfigName(), CommonConstant.CONFIG_FILE_SUFFIX);
            redisConfigFile.deleteOnExit();
            Files.asCharSink(redisConfigFile, StandardCharsets.UTF_8)
                    .write(redisConfigBuilder.toString());
            redisConf = redisConfigFile.getAbsolutePath();
        }

        try {
            executable = redisExecProvider.get();
        } catch (Exception e) {
            log.warn("Failed to resolve executable. exception: {}", e.getMessage(), e);
            throw new RedisBuildingException("Failed to resolve executable", e);
        }
    }

    private String resolveConfigName() {
        return CONF_FILENAME + CommonConstant.SEPARATOR_UNDERSCORE + port;
    }

    private List<String> buildCommandArgs() {
        List<String> args = new ArrayList<>();
        args.add(executable.getAbsolutePath());

        if (StringUtils.isNotEmpty(redisConf)) {
            args.add(redisConf);
        }

        args.add("--port");
        args.add(Integer.toString(port));

        if (replicaOf != null) {
            args.add("--replicaof");
            args.add(replicaOf.getHostName());
            args.add(Integer.toString(replicaOf.getPort()));
        }

        if (clusterEnable) {
            args.add("--cluster-enabled");
            args.add("yes");
        }

        if (slaveOf != null) {
            args.add("--slaveof");
            args.add(slaveOf.getHostName());
            args.add(Integer.toString(slaveOf.getPort()));
        }

        if (sentinelEnable) {
            args.add("--sentinel");
        }

        args.add("--loglevel");
        args.add("debug");

        args.add("--daemonize");
        args.add("no");

        return args;
    }
}
