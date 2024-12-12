package redis.embedded;

import com.google.common.io.Files;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.embedded.common.CommonConstant;
import redis.embedded.exceptions.RedisBuildingException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class RedisServerBuilder {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CONF_FILENAME = "embedded-redis-server";
    private File executable;
    private RedisExecProvider redisExecProvider = RedisServerExecProvider.defaultProvider();
    private String bind = CommonConstant.ALL_REDIS_HOST;
    private Integer port = CommonConstant.DEFAULT_REDIS_STANDALONE_PORT;

    //集群模式参数
    private Boolean clusterEnable = Boolean.FALSE;

    //哨兵模式参数
    private Boolean sentinelEnable = Boolean.FALSE;

    //主从模式参数
    private RedisEndpoint replicaOf;

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

    public RedisServerBuilder port(Integer port) {
        this.port = port;
        return this;
    }

    public RedisServerBuilder clusterEnable(Boolean enable) {
        this.clusterEnable = enable;
        return this;
    }

    public RedisServerBuilder sentinelEnable(Boolean enable) {
        this.sentinelEnable = enable;
        return this;
    }

    public RedisServerBuilder replicaOf(Integer port) {
        this.replicaOf = new RedisEndpoint(CommonConstant.DEFAULT_REDIS_HOST, port);
        return this;
    }

    public RedisServerBuilder replicaOf(RedisEndpoint replicaOf) {
        this.replicaOf = replicaOf;
        return this;
    }


    public RedisServerBuilder configFile(String redisConf) {
        if (redisConfigBuilder != null) {
            String msg = "Redis configuration is already partially build using setting(String) method!";
            log.warn(msg);
            throw new RedisBuildingException(msg);
        }
        this.redisConf = redisConf;
        return this;
    }

    public RedisServerBuilder setting(String configLine) {
        if (redisConf != null) {
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
        this.clusterEnable = Boolean.FALSE;
        this.sentinelEnable = Boolean.FALSE;
    }

    private void tryResolveConfAndExec() {
        try {
            resolveConfAndExec();
        } catch (IOException e) {
            String msg = "Could not build server instance";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new RedisBuildingException(msg, e);
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
            String msg = "Failed to resolve executable";
            log.warn("{}. exception: {}", msg, e.getMessage(), e);
            throw new RedisBuildingException(msg, e);
        }
    }

    private String resolveConfigName() {
        return CONF_FILENAME + CommonConstant.SEPARATOR_UNDERSCORE + port;
    }

    private List<String> buildCommandArgs() {
        List<String> args = new LinkedList<>();
        args.add(executable.getAbsolutePath());

        if (StringUtils.isNotEmpty(redisConf)) {
            args.add(redisConf);
        }

        args.add("--port");
        args.add(Integer.toString(port));

        if (replicaOf != null) {
            args.add("--replicaof");
            args.add(replicaOf.getHost());
            args.add(Integer.toString(replicaOf.getPort()));
        }

        if (clusterEnable) {
            args.add("--cluster-enabled");
            args.add("yes");
        }

        if (sentinelEnable) {
            args.add("--sentinel");
        }

        args.add("--loglevel");
        args.add("debug");

        args.add("--daemonize");
        args.add("no");

        args.add("--protected-mode");
        args.add("no");

        return args;
    }
}
