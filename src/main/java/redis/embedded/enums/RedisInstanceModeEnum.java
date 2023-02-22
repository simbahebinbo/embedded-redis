package redis.embedded.enums;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

// redis实例的模式
@Getter
@AllArgsConstructor
public enum RedisInstanceModeEnum {
    INVALID(0, "invalid"),
    CLIENT(1, "client"),
    SERVER(2, "server"),
    SENTINEL(3, "sentinel"),
    ;

    private static final ImmutableMap<String, RedisInstanceModeEnum> valueMapping;
    private static final ImmutableMap<Integer, RedisInstanceModeEnum> codeMapping;

    static {
        Map<String, RedisInstanceModeEnum> valueMap = new HashMap<>();
        Map<Integer, RedisInstanceModeEnum> codeMap = new HashMap<>();

        RedisInstanceModeEnum[] values = RedisInstanceModeEnum.values();
        for (RedisInstanceModeEnum value : values) {
            valueMap.put(value.getValue(), value);
            codeMap.put(value.getCode(), value);
        }

        valueMapping = ImmutableMap.copyOf(valueMap);
        codeMapping = ImmutableMap.copyOf(codeMap);
    }

    private final Integer code;
    private final String value;

    public static RedisInstanceModeEnum find(final Integer code) {
        return codeMapping.getOrDefault(code, INVALID);
    }

    public static RedisInstanceModeEnum find(final String value) {
        return valueMapping.getOrDefault(value, INVALID);
    }
}