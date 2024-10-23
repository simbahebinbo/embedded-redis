package redis.embedded.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

    /**
     * 分隔符
     */
    public static final String SEPARATOR_SEMICOLON = ";";

    public static final String SEPARATOR_QUESTION_MARK = "?";

    public static final String SEPARATOR_COLON = ":";

    public static final String SEPARATOR_COLON2 = "::";

    public static final String SEPARATOR_COMMA = ",";

    public static final String SEPARATOR_AMPERSAND = "&";

    public static final String SEPARATOR_EQUAL_SIGN = "=";

    /**
     * 斜线
     */
    public static final String SEPARATOR_VIRGULE = "/";

    public static final String SEPARATOR_BLANK = "";

    public static final String SEPARATOR_ASTERISK = "*";

    /**
     * 竖线
     */
    public static final String SEPARATOR_VERTICAL_LINE = "|";

    /**
     * 下划线
     */
    public static final String SEPARATOR_UNDERSCORE = "_";
    /**
     * 连字符 中横线
     */
    public static final String SEPARATOR_HYPHEN = "-";

    public static final String CONFIG_FILE_SUFFIX = ".conf";

    /**
     * 默认redis的端口和地址
     */
    public static final int DEFAULT_REDIS_PORT = 6379;

    public static final int DEFAULT_REDIS_STANDALONE_PORT = DEFAULT_REDIS_PORT;

    public static final int DEFAULT_REDIS_MASTER_PORT = DEFAULT_REDIS_PORT;
    public static final int DEFAULT_REDIS_SENTINEL_PORT = 26379;

    public static final String DEFAULT_REDIS_HOST = "127.0.0.1";
    public static final String ALL_REDIS_HOST = "0.0.0.0";

    /**
     * 操作系统
     */
    public static final String OS_NIX = "nix";

    public static final String OS_NUX = "nux";
    public static final String OS_AIX = "aix";
    public static final String OS_MAC_OSX = "Mac OS X";

    public static final String OS_NAME_MAC_OSX = "darwin";

    public static final String OS_NAME_LINUX = "linux";


    //处理器架构
    public static final String ARCHITECTURE_AARCH64 = "aarch64";

    public static final String ARCHITECTURE_ARM64 = "arm64";

    public static final String ARCHITECTURE_X86_64 = "x86_64";

    public static final String ARCHITECTURE_NAME_ARM64 = "arm64";
    public static final String ARCHITECTURE_NAME_AMD64 = "amd64";


    /**
     * redis 二进制文件
     */
    public static final String REDIS_SERVER = "redis-server";

    public static final String REDIS_SENTINEL = "redis-sentinel";
    public static final String REDIS_CLI = "redis-cli";

    public static final String REDIS_VERSION = "7.4.1";

    public static final String REDIS_SERVER_EXEC_UNIX_AMD64 =
            REDIS_SERVER + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;

    public static final String REDIS_SERVER_EXEC_UNIX_ARM64 =
            REDIS_SERVER + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;
    public static final String REDIS_SERVER_EXEC_MAC_OSX_AMD64 =
            REDIS_SERVER + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;

    public static final String REDIS_SERVER_EXEC_MAC_OSX_ARM64 =
            REDIS_SERVER + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;


    public static final String REDIS_SENTINEL_EXEC_UNIX_AMD64 =
            REDIS_SENTINEL + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;
    public static final String REDIS_SENTINEL_EXEC_UNIX_ARM64 =
            REDIS_SENTINEL + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;
    public static final String REDIS_SENTINEL_EXEC_MAC_OSX_AMD64 =
            REDIS_SENTINEL + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;

    public static final String REDIS_SENTINEL_EXEC_MAC_OSX_ARM64 =
            REDIS_SENTINEL + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;


    public static final String REDIS_CLI_EXEC_UNIX_AMD64 =
            REDIS_CLI + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;
    public static final String REDIS_CLI_EXEC_UNIX_ARM64 =
            REDIS_CLI + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_LINUX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;
    public static final String REDIS_CLI_EXEC_MAC_OSX_AMD64 =
            REDIS_CLI + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_AMD64;

    public static final String REDIS_CLI_EXEC_MAC_OSX_ARM64 =
            REDIS_CLI + SEPARATOR_HYPHEN + REDIS_VERSION + SEPARATOR_HYPHEN + OS_NAME_MAC_OSX + SEPARATOR_HYPHEN + ARCHITECTURE_NAME_ARM64;
}
