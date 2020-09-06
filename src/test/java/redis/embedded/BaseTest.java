package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import redis.clients.jedis.Jedis;

@Slf4j
public class BaseTest {
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

  protected void setUp() {}

  protected void writeSuccess(Jedis jedis) {
    msetSuccess(jedis);
    setSuccess(jedis);
  }

  private void msetSuccess(Jedis jedis) {
    key1 = RandomStringUtils.randomAlphabetic(5, 100);
    key2 = RandomStringUtils.randomAlphabetic(5, 100);
    key3 = RandomStringUtils.randomAlphabetic(5, 100);

    value1 = RandomStringUtils.randomAlphanumeric(5, 100);
    value2 = RandomStringUtils.randomAlphanumeric(5, 100);
    value3 = RandomStringUtils.randomAlphanumeric(5, 100);

    log.info("key1:" + key1 + "   value1:" + value1);
    log.info("key2:" + key2 + "   value2:" + value2);
    log.info("key3:" + key3 + "   value3:" + value3);
    jedis.mset(key1, value1, key2, value2);
    Assertions.assertTrue(true);
  }

  private void setSuccess(Jedis jedis) {
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

  protected void writeFail(Jedis jedis) {
    msetFail(jedis);
    setFail(jedis);
  }

  private void msetFail(Jedis jedis) {
    log.info("key1:" + key1 + "   value1:" + value1);
    log.info("key2:" + key2 + "   value2:" + value2);
    log.info("key3:" + key3 + "   value3:" + value3);
    Assertions.assertThrows(Exception.class, () -> jedis.mset(key1, value1, key2, value2));
  }

  private void setFail(Jedis jedis) {
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
    String newValue1 = jedis.mget(key1).get(0);
    String newValue2 = jedis.mget(key2).get(0);
    String newValue3 = jedis.mget(key3).get(0);
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
}
