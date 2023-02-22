package redis.embedded.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTool {

    private static final Clock clock = Clock.systemDefaultZone();

    public static long currentTimeMillis() {

        return clock.millis();
    }

    // 当前时间(纳秒)
    public static long currentTimeNanos() {

        return System.nanoTime();
    }

    // 将纳秒转成毫秒 保留精度
    public static double convertNanosToMillis(long nanos) {

        return (double) nanos / 1000 / 1000D;
    }
}
