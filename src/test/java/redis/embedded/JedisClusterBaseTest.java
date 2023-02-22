package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.JedisCluster;

@Slf4j
public class JedisClusterBaseTest {
    String key4;
    String key5;
    String key6;
    String value4;
    String value5;
    String value6;

    protected void setUp() {
    }

    protected void writeSuccess(JedisCluster jedis) {
        setSuccess(jedis);
    }

    private void setSuccess(JedisCluster jedis) {
        key4 = RandomStringUtils.randomAlphabetic(5, 100);
        key5 = RandomStringUtils.randomAlphabetic(5, 100);
        key6 = RandomStringUtils.randomAlphabetic(5, 100);

        value4 = RandomStringUtils.randomAlphanumeric(5, 100);
        value5 = RandomStringUtils.randomAlphanumeric(5, 100);
        value6 = RandomStringUtils.randomAlphanumeric(5, 100);

        log.info("key4:" + key4 + "   value4:" + value4);
        log.info("key5:" + key5 + "   value5:" + value5);
        log.info("key6:" + key6 + "   value6:" + value6);
        jedis.set(key4, value4);
        jedis.set(key5, value5);
        Assertions.assertTrue(true);
    }

    protected void writeFail(JedisCluster jedis) {
        setFail(jedis);
    }

    private void setFail(JedisCluster jedis) {
        key4 = RandomStringUtils.randomAlphabetic(5, 100);
        key5 = RandomStringUtils.randomAlphabetic(5, 100);
        key6 = RandomStringUtils.randomAlphabetic(5, 100);

        value4 = RandomStringUtils.randomAlphanumeric(5, 100);
        value5 = RandomStringUtils.randomAlphanumeric(5, 100);
        value6 = RandomStringUtils.randomAlphanumeric(5, 100);
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

    protected void readSuccess(JedisCluster jedis) {
        getSuccess(jedis);
    }

    private void getSuccess(JedisCluster jedis) {
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

    protected void readFail(JedisCluster jedis) {
        getFail(jedis);
    }

    private void getFail(JedisCluster jedis) {
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

    protected void readNothing(JedisCluster jedis) {
        getNothing(jedis);
    }

    private void getNothing(JedisCluster jedis) {
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
