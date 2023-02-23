package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.common.CommonConstant;
import redis.embedded.util.OSArchitecture;

@Slf4j
public class RedisServerExecProvider extends RedisExecProvider {

    private RedisServerExecProvider() {
        initExecutables();
    }

    public static RedisServerExecProvider defaultProvider() {
        return new RedisServerExecProvider();
    }

    @Override
    protected void initExecutables() {
        executables.put(OSArchitecture.UNIX_AMD64, CommonConstant.REDIS_SERVER_EXEC_UNIX_AMD64);
        executables.put(OSArchitecture.UNIX_ARM64, CommonConstant.REDIS_SERVER_EXEC_UNIX_ARM64);

        executables.put(OSArchitecture.MAC_OSX_AMD64, CommonConstant.REDIS_SERVER_EXEC_MAC_OSX_AMD64);
        executables.put(OSArchitecture.MAC_OSX_ARM64, CommonConstant.REDIS_SERVER_EXEC_MAC_OSX_ARM64);
    }
}
