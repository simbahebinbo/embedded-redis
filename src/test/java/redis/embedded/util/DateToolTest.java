package redis.embedded.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class DateToolTest {

    @Test
    public void testIntervalMillis() {
        long interval = RandomUtils.secure().randomLong(1, 100);
        long currentTime = DateTool.currentTimeMillis();
        Awaitility.await().pollDelay(interval, TimeUnit.MILLISECONDS).until(() -> true);
        long newTime = DateTool.currentTimeMillis();
        Assertions.assertTrue(newTime - currentTime >= interval);
    }

    @Test
    public void testIntervalNanos() {
        long interval = RandomUtils.secure().randomLong(1, 100);
        long currentTime = DateTool.currentTimeNanos();
        Awaitility.await().pollDelay(interval, TimeUnit.NANOSECONDS).until(() -> true);
        long newTime = DateTool.currentTimeNanos();
        Assertions.assertTrue(newTime - currentTime >= interval);
    }

    @Test
    public void testConvertNanosToMillis() {
        long currentTimeMillis = DateTool.currentTimeMillis();
        long currentTimeNanos = DateTool.currentTimeNanos();
        Awaitility.await()
                .pollDelay(RandomUtils.secure().randomLong(1, 100), TimeUnit.MILLISECONDS)
                .until(() -> true);
        Awaitility.await()
                .pollDelay(RandomUtils.secure().randomLong(1, 100), TimeUnit.NANOSECONDS)
                .until(() -> true);
        long newTimeMillis = DateTool.currentTimeMillis();
        long newTimeNanos = DateTool.currentTimeNanos();

        long intervalMillis = newTimeMillis - currentTimeMillis;
        long intervalNanos = newTimeNanos - currentTimeNanos;
        Assertions.assertTrue(
                ((long) DateTool.convertNanosToMillis(intervalNanos) == intervalMillis + 1)
                        || (((long) DateTool.convertNanosToMillis(intervalNanos) == intervalMillis))
                        || (((long) DateTool.convertNanosToMillis(intervalNanos) == intervalMillis - 1)));
    }
}
