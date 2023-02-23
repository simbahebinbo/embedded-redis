package redis.embedded;

import com.google.common.io.Files;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
public class RedisServerBuilder {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String CONF_FILENAME = "embedded-redis-server";
    private File executable;
    private RedisExecProvider redisExecProvider = RedisServerExecProvider.defaultProvider();
    private String bind = CommonConstant.ALL_REDIS_HOST;
    private int port = CommonConstant.DEFAULT_REDIS_STANDALONE_PORT;
    private int tlsPort = 0;

    //主从模式参数
    private InetSocketAddress slaveOf;

    //集群模式参数
    private Boolean clusterEnable = false;

    //哨兵模式参数
    private Boolean sentinelEnable = false;

    private InetSocketAddress replicaOf;

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

    public RedisServerBuilder tlsPort(int tlsPort) {
        this.tlsPort = tlsPort;
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

    public RedisServerBuilder slaveOf(int port) {
        this.slaveOf = new InetSocketAddress(CommonConstant.DEFAULT_REDIS_HOST, port);
        return this;
    }

    public RedisServerBuilder slaveOf(InetSocketAddress slaveOf) {
        this.slaveOf = slaveOf;
        return this;
    }

    public RedisServerBuilder replicaOf(int port) {
        this.replicaOf = new InetSocketAddress(CommonConstant.DEFAULT_REDIS_HOST, port);
        return this;
    }

    public RedisServerBuilder replicaOf(InetSocketAddress replicaOf) {
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
        return new RedisServer(args, port, tlsPort);
    }

    public void reset() {
        this.executable = null;
        this.redisConfigBuilder = null;
        this.replicaOf = null;
        this.redisConf = null;
        this.clusterEnable = false;
        this.slaveOf = null;
        this.sentinelEnable = false;
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
        List<String> args = new ArrayList<>();
        args.add(executable.getAbsolutePath());

        if (StringUtils.isNotEmpty(redisConf)) {
            args.add(redisConf);
        }

        args.add("--port");
        args.add(Integer.toString(port));

        if (tlsPort > 0) {
            args.add("--tls-port");
            args.add(Integer.toString(tlsPort));
        }

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

        args.add("--protected-mode");
        args.add("no");

        return args;
    }
}
