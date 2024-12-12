package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.Jedis;

@Slf4j
public class JedisBaseTest {
    String key1;
    String key2;
    String key3;
    String key4;
    String key5;
    String key6;
    String value1;
    String value2;
    String value3;
    String value4;
    String value5;
    String value6;

    protected void setUp() {
    }

    protected void writeSuccess(Jedis jedis) {
        msetSuccess(jedis);
        setSuccess(jedis);
    }

    private void msetSuccess(Jedis jedis) {
        key1 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key2 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key3 = RandomStringUtils.secure().nextAlphabetic(5, 100);

        value1 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value2 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value3 = RandomStringUtils.secure().nextAlphanumeric(5, 100);

        log.info("key1:" + key1 + "   value1:" + value1);
        log.info("key2:" + key2 + "   value2:" + value2);
        log.info("key3:" + key3 + "   value3:" + value3);
        jedis.mset(key1, value1, key2, value2);
        Assertions.assertTrue(true);
    }

    private void setSuccess(Jedis jedis) {
        key4 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key5 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key6 = RandomStringUtils.secure().nextAlphabetic(5, 100);

        value4 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value5 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value6 = RandomStringUtils.secure().nextAlphanumeric(5, 100);

        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);
        jedis.set(key4, value4);
        jedis.set(key5, value5);
        Assertions.assertTrue(true);
    }

    protected void writeFail(Jedis jedis) {
        msetFail(jedis);
        setFail(jedis);
    }

    private void msetFail(Jedis jedis) {
        key1 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key2 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key3 = RandomStringUtils.secure().nextAlphabetic(5, 100);

        value1 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value2 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value3 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        log.info("key1:" + key1 + "   value1:" + value1);
        log.info("key2:" + key2 + "   value2:" + value2);
        log.info("key3:" + key3 + "   value3:" + value3);
        Assertions.assertThrows(Exception.class, () -> jedis.mset(key1, value1, key2, value2));
    }

    private void setFail(Jedis jedis) {
        key4 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key5 = RandomStringUtils.secure().nextAlphabetic(5, 100);
        key6 = RandomStringUtils.secure().nextAlphabetic(5, 100);

        value4 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value5 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        value6 = RandomStringUtils.secure().nextAlphanumeric(5, 100);
        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);

        Assertions.assertThrows(
                Exception.class,
                () -> {
                    jedis.set(key4, value4);
                    jedis.set(key5, value5);
                });
    }

    protected void readSuccess(Jedis jedis) {
        mgetSuccess(jedis);
        getSuccess(jedis);
    }

    private void mgetSuccess(Jedis jedis) {
        log.info("key1:" + key1 + "   value1:" + value1);
        log.info("key2:" + key2 + "   value2:" + value2);
        log.info("key3:" + key3 + "   value3:" + value3);
        String newValue1 = jedis.mget(key1).getFirst();
        String newValue2 = jedis.mget(key2).getFirst();
        String newValue3 = jedis.mget(key3).getFirst();
        log.info("key1:" + key1 + "   newValue1:" + newValue1);
        log.info("key2:" + key2 + "   newValue2:" + newValue2);
        log.info("key3:" + key3 + "   newValue3:" + newValue3);
        Assertions.assertEquals(value1, newValue1);
        Assertions.assertEquals(value2, newValue2);
        Assertions.assertNull(newValue3);
    }

    private void getSuccess(Jedis jedis) {
        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);
        String newValue4 = jedis.get(key4);
        String newValue5 = jedis.get(key5);
        String newValue6 = jedis.get(key6);
        log.info("key4:" + key4 + "   newValue4:" + newValue4);
        log.info("key5:" + key5 + "   newValue5:" + newValue5);
        log.info("key6:" + key6 + "   newValue6:" + newValue6);
        Assertions.assertEquals(value4, newValue4);
        Assertions.assertEquals(value5, newValue5);
        Assertions.assertNull(newValue6);
    }

    protected void readFail(Jedis jedis) {
        mgetFail(jedis);
        getFail(jedis);
    }

    private void mgetFail(Jedis jedis) {
        log.info("key1:" + key1 + "   value1:" + value1);
        log.info("key2:" + key2 + "   value2:" + value2);
        log.info("key3:" + key3 + "   value3:" + value3);
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    jedis.mget(key1);
                    jedis.mget(key2);
                    jedis.mget(key3);
                });
    }

    private void getFail(Jedis jedis) {
        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    jedis.get(key4);
                    jedis.get(key5);
                    jedis.get(key6);
                });
    }

    protected void readNothing(Jedis jedis) {
        mgetNothing(jedis);
        getNothing(jedis);
    }

    private void mgetNothing(Jedis jedis) {
        log.info("key1:" + key1 + "   value1:" + value1);
        log.info("key2:" + key2 + "   value2:" + value2);
        log.info("key3:" + key3 + "   value3:" + value3);

        String newValue1 = jedis.mget(key1).getFirst();
        String newValue2 = jedis.mget(key2).getFirst();
        String newValue3 = jedis.mget(key3).getFirst();
        log.info("key1:" + key1 + "   newValue1:" + newValue1);
        log.info("key2:" + key2 + "   newValue2:" + newValue2);
        log.info("key3:" + key3 + "   newValue3:" + newValue3);
        Assertions.assertNull(newValue1);
        Assertions.assertNull(newValue2);
        Assertions.assertNull(newValue3);
    }

    private void getNothing(Jedis jedis) {
        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);
        String newValue4 = jedis.get(key4);
        String newValue5 = jedis.get(key5);
        String newValue6 = jedis.get(key6);
        log.info("key4:" + key4 + "   newValue4:" + newValue4);
        log.info("key5:" + key5 + "   newValue5:" + newValue5);
        log.info("key6:" + key6 + "   newValue6:" + newValue6);
        Assertions.assertNull(newValue4);
        Assertions.assertNull(newValue5);
        Assertions.assertNull(newValue6);
    }
}
