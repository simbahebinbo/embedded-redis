package redis.embedded.util;

import java.time.Clock;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    double millis = nanos / 1000 / 1000D;

    return millis;
  }
}
