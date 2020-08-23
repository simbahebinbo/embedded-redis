package redis.embedded;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModeStandaloneTest extends BaseTest {

  private RedisServer redisServer;

  //	@Test
  //	public void testSimpleOperationsAfterRun() throws Exception {
  //		redisServer = new RedisServer(6379);
  //		redisServer.start();
  //
  //		JedisPool pool = null;
  //		Jedis jedis = null;
  //		try {
  //			pool = new JedisPool("localhost", 6379);
  //			jedis = pool.getResource();
  //			jedis.mset("abc", "1", "def", "2");
  //
  //			assertEquals("1", jedis.mget("abc").get(0));
  //			assertEquals("2", jedis.mget("def").get(0));
  //			assertNull(jedis.mget("xyz").get(0));
  //		} finally {
  //			if (jedis != null)
  //				pool.returnResource(jedis);
  //			redisServer.stop();
  //		}
  //	}
}
