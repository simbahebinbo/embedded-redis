package redis.embedded;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

@Slf4j
public class BaseTest {
  String key1;
  String key2;
  String key3;
  String value1;
  String value2;
  String value3;

  @BeforeEach
  public void setUp() {
    key1 = RandomStringUtils.randomAlphabetic(5, 10);
    key2 = RandomStringUtils.randomAlphabetic(5, 10);
    key3 = RandomStringUtils.randomAlphabetic(5, 10);

    value1 = RandomStringUtils.randomAlphanumeric(5, 10);
    value2 = RandomStringUtils.randomAlphanumeric(5, 10);
    value3 = RandomStringUtils.randomAlphanumeric(5, 10);
  }

  @Test
  public void writeSuccess(Jedis jedis) {
    jedis.mset(key1, value1, key2, value2);
    Assertions.assertTrue(true);
  }

  @Test
  public void writeFail(Jedis jedis) {
    Assertions.assertThrows(Exception.class, () -> jedis.mset(key1, value1, key2, value2));
  }

  @Test
  public void readSuccess(Jedis jedis) {
    String newValue1 = jedis.mget(key1).get(0);
    String newValue2 = jedis.mget(key2).get(0);
    String newValue3 = jedis.mget(key3).get(0);
    Assertions.assertEquals(value1, newValue1);
    Assertions.assertEquals(value2, newValue2);
    Assertions.assertNull(newValue3);
  }

  @Test
  public void readFail(Jedis jedis) {
    Assertions.assertThrows(
        Exception.class,
        () -> {
          jedis.mget(key1).get(0);
          jedis.mget(key2).get(0);
          jedis.mget(key3).get(0);
        });
  }
}
